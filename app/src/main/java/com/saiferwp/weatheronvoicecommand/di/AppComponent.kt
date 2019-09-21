package com.saiferwp.weatheronvoicecommand.di

import com.saiferwp.weatheronvoicecommand.data.LocationProvider
import com.saiferwp.weatheronvoicecommand.data.WeatherDataProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        WeatherDataModule::class,
        LocationModule::class
    ]
)
interface AppComponent {
    fun getWeatherDataProvider(): WeatherDataProvider
    fun getLocationProvider(): LocationProvider
}
