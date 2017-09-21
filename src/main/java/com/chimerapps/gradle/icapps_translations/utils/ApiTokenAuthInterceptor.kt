package com.chimerapps.gradle.icapps_translations.utils

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response


/**
 * @author Nicola Verbeeck
 * @date 18/09/2017.
 */
class ApiTokenAuthInterceptor : Interceptor {

    var apiToken: String = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
                .header("Authorization", Credentials.basic("api", apiToken)).build()
        return chain.proceed(authenticatedRequest)
    }

}