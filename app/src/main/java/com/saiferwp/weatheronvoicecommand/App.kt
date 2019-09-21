package com.saiferwp.weatheronvoicecommand

import android.app.Application
import com.saiferwp.weatheronvoicecommand.di.AppComponent
import com.saiferwp.weatheronvoicecommand.di.ContextModule
import com.saiferwp.weatheronvoicecommand.di.DaggerAppComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initDaggerComponent()
    }

    private fun initDaggerComponent() {
        component = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }

    companion object {
        lateinit var component: AppComponent private set
    }
}