package com.example.android.dessertpusher

import android.os.Bundle

object Persistent {

    private const val PREFS_NAME  = "APP_STATE"

    private const val KEY_DESSERT = "dessert"
    private const val KEY_SOLD    = "sold"
    private const val KEY_REVENUE = "revenue"
    private const val KEY_TIMER   = "timer"

    private val DESERTS_KEYS = setOf(KEY_DESSERT, KEY_SOLD, KEY_REVENUE)

    private val prefs = DessertApp.instance.getSharedPreferences(PREFS_NAME, 0)
    private val editor = prefs.edit()

    fun save(block: Persistent.()->Unit) {
        this.block()
        editor.apply()
    }

    fun dessert(value: Int)     { editor.putInt(KEY_DESSERT, value) }
    fun sold(value: Int)        { editor.putInt(KEY_SOLD, value) }
    fun revenue(value: Int)     { editor.putInt(KEY_REVENUE, value) }
    fun timer(value: String)    { editor.putString(KEY_TIMER, value) }

    fun hasDessert(bundle: Bundle)= bundle.keySet().containsAll(DESERTS_KEYS)

    fun dessert(from: Bundle)  = from.getInt(KEY_DESSERT)
    fun sold(from: Bundle)     = from.getInt(KEY_SOLD)
    fun revenue(from: Bundle)  = from.getInt(KEY_REVENUE)
    fun timer(from: Bundle) = from.getString(KEY_TIMER)

    fun asBundle() = Bundle().apply {
        if (prefs.contains(KEY_DESSERT))  putInt(KEY_DESSERT, prefs.getInt(KEY_DESSERT, 0))
        if (prefs.contains(KEY_SOLD))     putInt(KEY_SOLD, prefs.getInt(KEY_SOLD, 0))
        if (prefs.contains(KEY_REVENUE))  putInt(KEY_REVENUE, prefs.getInt(KEY_REVENUE, 0))
        if (prefs.contains(KEY_TIMER))    putString(KEY_TIMER, prefs.getString(KEY_TIMER, "0"))
    }

}
