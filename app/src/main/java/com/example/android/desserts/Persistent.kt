package com.example.android.desserts

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

object Persistent : LifecycleObserver {

    private const val PREFS_NAME  = "APP_STATE"

    private const val KEY_EATEN   = "eaten"
    private const val KEY_SPENT   = "spent"
    private const val KEY_CLOCK   = "clock"

    private val prefs = DesApp.instance.getSharedPreferences(PREFS_NAME, 0)
    private val editor = prefs.edit()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun save() = editor.commit()

    fun eaten(value: Int)       { editor.putInt(KEY_EATEN, value) }
    fun spent(value: Int)       { editor.putInt(KEY_SPENT, value) }
    fun clock(value: String)    { editor.putString(KEY_CLOCK, value) }

    fun eaten(from: Bundle, def:Int = 0)    = from.getInt(KEY_EATEN, def)
    fun spent(from: Bundle, def:Int = 0)    = from.getInt(KEY_SPENT, def)
    fun clock(from: Bundle)              = from.getString(KEY_CLOCK)

    fun asBundle() = Bundle().apply {
        if (prefs.contains(KEY_EATEN))    putInt(KEY_EATEN, prefs.getInt(KEY_EATEN, 0))
        if (prefs.contains(KEY_SPENT))    putInt(KEY_SPENT, prefs.getInt(KEY_SPENT, 0))
        if (prefs.contains(KEY_CLOCK))    putString(KEY_CLOCK, prefs.getString(KEY_CLOCK, "0"))
    }

}
