package com.saiferwp.weatheronvoicecommand.data.api

import com.saiferwp.weatheronvoicecommand.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val baseHost = request.url()
        val requestBuilder = request.newBuilder()
        requestBuilder.url(
            baseHost.newBuilder().setQueryParameter(
                "appid", BuildConfig.OpenWeatherMapApiKey
            ).build()
        )

        return chain.proceed(requestBuilder.build())
    }
}