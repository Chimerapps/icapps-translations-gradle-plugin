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
import org.gradle.api.logging.Logger
import java.io.File
import java.io.FileOutputStream

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

        val token = makeToken(configuration)

        val languages = translationsAPI.getLanguages(token).execute()

        logger.debug("Get languages result: ${languages.message()} and code ${languages.code()}. Body: ${languages.body()}")
        val projectLanguages = languages.body() ?: throw IllegalArgumentException("Failed to load list of languages")

        logger.debug("Got ${projectLanguages.size} language files")
        projectLanguages.forEach {
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

            FileOutputStream(targetFile).apply {
                val size = body.byteStream().copyTo(this)
                logger.debug("Translation file for ${it.languageCode} saved, $size bytes")
            }
        }
    }

    private fun makeToken(configuration: TranslationConfiguration): String {
        return "Token token=${configuration.apiKey}"
    }

}