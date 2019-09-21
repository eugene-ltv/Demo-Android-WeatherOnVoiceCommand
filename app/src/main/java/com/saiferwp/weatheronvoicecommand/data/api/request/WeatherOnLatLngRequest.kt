package com.saiferwp.weatheronvoicecommand.data.api.request

import com.saiferwp.weatheronvoicecommand.data.api.Api
import com.saiferwp.weatheronvoicecommand.data.api.response.WeatherOnLatLngResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response

class WeatherOnLatLngRequest(
    private val latitude: Double,
    private val longitude: Double
) : Request<WeatherOnLatLngResponse>() {
    override fun executeAsync(api: Api): Deferred<Response<WeatherOnLatLngResponse>> {
        return api.getWeatherOnLatLngAsync(latitude.toString(), longitude.toString())
    }
}
