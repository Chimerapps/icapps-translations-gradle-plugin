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

package com.chimerapps.gradle.icapps_translations

import com.chimerapps.gradle.icapps_translations.utils.exists
import groovy.lang.Closure
import org.gradle.api.Action

/**
 * @author Nicola Verbeeck
 * @date 04/09/2017.
 */
open class TranslationConfiguration(open var name: String = "default") {

    open var apiKey: String? = null
    open var fileName: String = "strings.xml"
    open var sourceRoot: String = "src/main/res"
    open var fileType: String = "xml"

    open var sourceRootProvider: Closure<String> = object : Closure<String>(null) {
        override fun call(vararg args: Any?): String {
            val code = (args[0] as String)
            return sourceRoot.replace("{language}", languageRename.call(code) ?: code)
        }
    }
    open var languageRename: Closure<String> = object : Closure<String>(null) {
        override fun call(vararg args: Any?): String {
            return (args[0] as String)
        }
    }
    open var fileNameProvider: Closure<String> = object : Closure<String>(null) {
        override fun call(vararg args: Any?): String {
            return fileName
        }
    }
    open var folderProvider: Closure<String> = object : Closure<String>(null) {
        override fun call(vararg args: Any?): String {
            val code = (args[0] as String)
            return "values-${languageRename.call(code) ?: code}"
        }
    }

    override fun toString(): String {
        return "TranslationConfiguration(name='$name', apiKey=$apiKey, fileName='$fileName', sourceRoot='$sourceRoot', fileType='$fileType', sourceRootProvider=$sourceRootProvider, languageRename=$languageRename, fileNameProvider=$fileNameProvider, folderProvider=$folderProvider)"
    }

}

open class DownloadTranslationsExtension : TranslationConfiguration() {

    val configurations = arrayListOf<TranslationConfiguration>()

    open fun configuration(name: String, configuration: Action<in TranslationConfiguration>) {
        if (configurations.exists { it.name == name })
            throw IllegalArgumentException("A configuration with the name \"$name\" already exists")
        if (name == "default")
            throw IllegalArgumentException("Creating a configuration with the name \"default\" is not allowed. The outer block is default")

        TranslationConfiguration(name).apply {
            configuration.execute(this)
            configurations.add(this)
        }
    }

}