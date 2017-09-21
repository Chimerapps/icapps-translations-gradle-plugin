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

package com.chimerapps.gradle.icapps_translations.icapps_translations.api

import com.chimerapps.gradle.icapps_translations.icapps_translations.api.model.Language
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * @author Nicola Verbeeck
 * @date 04/09/2017.
 */
interface TranslationsAPI {

    companion object {
        const val API_BASE = "http://translations.icapps.com/api/"
    }

    @GET("languages.json")
    fun getLanguages(@Header("Authorization") authToken: String): Call<List<Language>>

    @GET("translations/{language}.{type}")
    fun getTranslation(
            @Header("Authorization") authToken: String,
            @Path("language") language: String,
            @Path("type") type: String): Call<ResponseBody>

}