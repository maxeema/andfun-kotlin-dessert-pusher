package com.example.android.dessertpusher

import android.app.Application
import timber.log.Timber

class DessertApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}