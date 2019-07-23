package com.example.android.dessertpusher

import android.app.Application
import timber.log.Timber

class DessertApp : Application() {

    companion object {
        private lateinit var appRef : DessertApp
        val instance
            get() = appRef
    }

    override fun onCreate() {
        super.onCreate()
        appRef = this
        Timber.plant(Timber.DebugTree())
    }

}