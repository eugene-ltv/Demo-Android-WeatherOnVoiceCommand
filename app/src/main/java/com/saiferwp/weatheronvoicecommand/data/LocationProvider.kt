package com.saiferwp.weatheronvoicecommand.data

import android.content.Context
import android.location.Location

class LocationProvider(
    val context: Context
) {
    companion object {
        val default = customLocation(52.509341, 13.374459)

        private fun customLocation(latitude: Double, longitude: Double): Location {
            return Location("").also {
                it.latitude = latitude
                it.longitude = longitude
            }
        }
    }
}