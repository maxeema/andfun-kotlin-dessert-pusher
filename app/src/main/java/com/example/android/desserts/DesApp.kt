package com.example.android.desserts

import android.app.Application
import timber.log.Timber

class DesApp : Application() {

    companion object {
        private lateinit var appRef : DesApp
        val instance
            get() = appRef
    }

    override fun onCreate() {
        super.onCreate()
        appRef = this
        Timber.plant(Timber.DebugTree())
    }

}