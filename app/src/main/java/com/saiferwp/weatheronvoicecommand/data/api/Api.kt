package com.saiferwp.weatheronvoicecommand.data.api

import com.saiferwp.weatheronvoicecommand.data.api.response.WeatherOnLatLngResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("weather")
    fun getWeatherOnLatLngAsync(
        @Query("lat") lat: String = "0.0",
        @Query("lon") lon: String = "0.0",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): Deferred<Response<WeatherOnLatLngResponse>>

}