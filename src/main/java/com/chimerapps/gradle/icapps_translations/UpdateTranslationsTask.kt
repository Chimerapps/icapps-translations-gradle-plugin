/*
 * Copyright 2017-2022 - Chimerapps BV
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

import com.chimerapps.gradle.icapps_translations.icapps_translations.TranslationDownloader
import com.chimerapps.gradle.icapps_translations.icapps_translations.api.LegacyTranslationsAPI
import com.chimerapps.gradle.icapps_translations.icapps_translations.api.NewTranslationsAPI
import com.chimerapps.gradle.icapps_translations.icapps_translations.api.TranslationsAPI
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import retrofit2.Retrofit

/**
 * @author Nicola Verbeeck
 * @date 04/09/2017-2022.
 */
open class UpdateTranslationsTask : DefaultTask() {

    private val apiBuilder: Retrofit.Builder by lazy { project.plugins.findPlugin(DownloadTranslationsPlugin::class.java)!!.retrofitBuilder }

    @get:Internal
    lateinit var configuration: TranslationConfiguration

    @TaskAction
    fun updateTranslations() {
        logger.debug("Update translations task running for config ${configuration.name}")

        if (configuration.apiKey == null && (configuration.projectKey == null && configuration.projectToken == null))
            throw IllegalArgumentException("No api key or project-key, project token pair provided for icappsTranslations")

        if (configuration.apiKey != null && (configuration.projectKey != null || configuration.projectToken != null)) {
            throw IllegalArgumentException("apiKey cannot be provided if projectKey or projectToken is set")
        }

        TranslationDownloader(createApi(configuration), logger).download(configuration, project)
    }

    private fun createApi(configuration: TranslationConfiguration): TranslationsAPI {
        if (configuration.apiKey != null) {
            return apiBuilder
                .baseUrl(LegacyTranslationsAPI.API_BASE)
                .build()
                .create(LegacyTranslationsAPI::class.java)
        }
        return apiBuilder.baseUrl(NewTranslationsAPI.API_BASE)
            .build()
            .create(NewTranslationsAPI::class.java)
    }

}