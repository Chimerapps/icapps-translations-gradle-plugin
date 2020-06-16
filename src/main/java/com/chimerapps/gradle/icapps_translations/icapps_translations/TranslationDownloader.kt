/*
 * Copyright 2017 - Chimerapps BVBA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.chimerapps.gradle.icapps_translations.icapps_translations

import com.chimerapps.gradle.icapps_translations.TranslationConfiguration
import com.chimerapps.gradle.icapps_translations.icapps_translations.api.TranslationsAPI
import groovy.lang.Closure
import groovy.xml.DOMBuilder
import org.gradle.api.logging.Logger
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.Reader
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * @author Nicola Verbeeck
 * @date 04/09/2017.
 */
class TranslationDownloader(private val translationsAPI: TranslationsAPI, private val logger: Logger) {

    fun download(configuration: TranslationConfiguration) {

        logger.debug("Downloading translations for project ($configuration)")

        val fileNameProvider = configuration.fileNameProvider
        val folderProvider = configuration.folderProvider
        val sourceRootProvider = configuration.sourceRootProvider
        val languageFilter = configuration.languageFilter
        val keyTransformer = configuration.keyTransformer

        val token = makeToken(configuration)
        logger.debug("Using token: $token")

        val languages = translationsAPI.getLanguages(token).execute()

        logger.debug("Get languages result: ${languages.message()} and code ${languages.code()}. Body: ${languages.body()}")
        val projectLanguages = languages.body() ?: throw IllegalArgumentException("Failed to load list of languages")

        logger.debug("Got ${projectLanguages.size} language files (before filter)")
        projectLanguages.filter { languageFilter.call(it.languageCode) }.forEach {

            val folderName = folderProvider.call(it.languageCode)
            val dir = File(sourceRootProvider.call(it.languageCode), folderName)
            dir.mkdirs()
            val targetFile = File(dir, fileNameProvider.call(it.languageCode))

            logger.debug("Downloading file for ${it.languageCode} to ${targetFile.absolutePath}")

            val translationFile = translationsAPI.getTranslation(token, it.languageCode, configuration.fileType).execute()

            val body = translationFile.body()
            if (body == null) {
                logger.warn("Failed to download file for locale ${it.languageCode}: ${translationFile.message()}")
                return@forEach
            }

            val dataStream = if (keyTransformer != null && configuration.fileType == "xml") {
                transformStream(body.charStream(), keyTransformer)
            } else {
                body.byteStream()
            }

            FileOutputStream(targetFile).apply {
                val size = dataStream.use { stream -> stream.copyTo(this) }
                logger.debug("Translation file for ${it.languageCode} saved, $size bytes")
            }
            try {
                body.close()
            } catch (e: Throwable) {
            }
        }
    }

    private fun transformStream(data: Reader, keyTransformer: Closure<CharSequence>): InputStream {
        val text = data.use { it.readText() }

        val builder = DOMBuilder.newInstance()
        val document = builder.parseText(text)

        val resources = document.documentElement
        resources.childNodes.forEach {
            if (it.nodeType == Node.ELEMENT_NODE) {
                val nameNode = it.attributes.getNamedItem("name")
                if (nameNode != null) {
                    val oldKey = nameNode.textContent
                    val transformed = keyTransformer.call(oldKey)
                    val newKey = transformed?.toString() ?: oldKey
                    nameNode.nodeValue = newKey
                }
            }
        }
        val targetStream = ByteArrayOutputStream()
        val result = StreamResult(targetStream)
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(DOMSource(document), result)

        return ByteArrayInputStream(targetStream.toByteArray())
    }

    private fun makeToken(configuration: TranslationConfiguration): String {
        return "Token token=${configuration.apiKey}"
    }

}

private inline fun NodeList.forEach(block: (Node) -> Unit) {
    for (i in 0 until this.length) {
        block(item(i))
    }
}