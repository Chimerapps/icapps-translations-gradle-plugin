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

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * @author Nicola Verbeeck
 * @date 04/09/2017-2022.
 */
open class DownloadTranslationsPlugin : Plugin<Project> {

    private val httpClient = OkHttpClient.Builder()
        .build()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(httpClient)

    override fun apply(target: Project) {
        val extension = target.extensions.create("icappsTranslations", DownloadTranslationsExtension::class.java)

        target.afterEvaluate {
            if (extension.configurations.isEmpty() && extension.apiKey == null && extension.projectKey == null) {
                target.logger.debug("No configurations or api/project key defined, not adding tasks")
                return@afterEvaluate
            }

            val tasks = arrayListOf<Task>()
            extension.configurations.forEach { configuration ->
                target.logger.debug("Creating task update${configuration.name.capitalize()}icappsTranslations")
                val task = target.tasks.create("update${configuration.name.capitalize()}icappsTranslations", UpdateTranslationsTask::class.java) {
                    it.configuration = configuration
                }
                task.group = "Translations"
                tasks.add(task)
            }

            if (extension.apiKey != null || (extension.projectKey != null && extension.projectToken != null)) {
                target.logger.debug("Creating task updateDefaulticappsTranslations")
                val task = target.tasks.create("updateDefaulticappsTranslations", UpdateTranslationsTask::class.java) {
                    it.configuration = extension
                }
                task.group = "Translations"
                tasks.add(task)
            }
            if (extension.keyTransformer != null && extension.fileType != "xml") {
                target.logger.error("Key transformer is currently only supported for xml files")
            }

            target.logger.debug("Creating task updateicappsTranslations")
            val allTask = target.tasks.create("updateicappsTranslations") {
                it.dependsOn.addAll(tasks)
            }
            allTask.group = "Translations"
        }

    }

}