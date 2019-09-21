package com.saiferwp.weatheronvoicecommand.di

import com.saiferwp.weatheronvoicecommand.data.WeatherDataProvider
import com.saiferwp.weatheronvoicecommand.data.api.ApiClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        ApiModule::class
    ]
)
open class WeatherDataModule {
    @Provides
    @Singleton
    open fun provideWeatherData(
        apiClient: ApiClient
    ) = WeatherDataProvider(apiClient)
}