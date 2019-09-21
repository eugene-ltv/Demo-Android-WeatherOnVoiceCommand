package com.saiferwp.weatheronvoicecommand.data

import com.saiferwp.weatheronvoicecommand.BuildConfig
import com.saiferwp.weatheronvoicecommand.data.api.ApiClient
import com.saiferwp.weatheronvoicecommand.data.api.request.WeatherOnLatLngRequest
import com.saiferwp.weatheronvoicecommand.data.api.response.WeatherOnLatLngResponse
import java.net.UnknownHostException

class WeatherDataProvider(
    private val apiClient: ApiClient
) {

    suspend fun getWeatherData(): WeatherOnLatLngResponse? {
        val location = LocationProvider.default

        try {
            val request = WeatherOnLatLngRequest(location.latitude, location.longitude)
            val response = apiClient.executeAsync(request).await()

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                if (body?.cod == 429) {
                    // Api key blocked
                    return null
                } else {
                    return body
                }
            } else {
                // Error, empty result
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                // No internet
            }
        }
        return null
    }

    fun getWebViewLink(cityId: Long): String {
        return "${BuildConfig.ApiHost}weather?" +
                "id=$cityId" +
                "&units=metric&mode=html" +
                "&appid=${BuildConfig.OpenWeatherMapApiKey}"
    }
}