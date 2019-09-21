package com.saiferwp.weatheronvoicecommand.data.api.response

data class WeatherOnLatLngResponse(
    val cod: Int,
    val id: Long,
    val name: String,
    val weather: List<Weather>,
    val main: Main
) {

    data class Weather (
        val main: String,
        val description: String
    )

    data class Main (
        val temp: Float,
        val temp_min: Float,
        val temp_max: Float,
        val pressure: Float,
        val humidity: Float
    )
}

