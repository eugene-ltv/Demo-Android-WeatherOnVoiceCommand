package com.saiferwp.weatheronvoicecommand.di

import android.content.Context
import com.saiferwp.weatheronvoicecommand.data.LocationProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        ContextModule::class
    ]
)
open class LocationModule {
    @Provides
    @Singleton
    open fun provideLocationProvider(
        context: Context
    ) = LocationProvider(context)
}