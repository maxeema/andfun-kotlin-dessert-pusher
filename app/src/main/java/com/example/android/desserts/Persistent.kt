package com.example.android.desserts

import android.os.Bundle

object Persistent {

    private const val PREFS_NAME  = "APP_STATE"

    private const val KEY_DESSERT = "dessert"
    private const val KEY_EATEN   = "eaten"
    private const val KEY_SPENT   = "spent"
    private const val KEY_CLOCK   = "clock"

    private val DESERTS_KEYS = setOf(KEY_DESSERT, KEY_EATEN, KEY_SPENT)

    private val prefs = DesApp.instance.getSharedPreferences(PREFS_NAME, 0)
    private val editor = prefs.edit()

    fun save(block: Persistent.()->Unit) {
        this.block()
        editor.apply()
    }

    fun dessert(value: Int)     { editor.putInt(KEY_DESSERT, value) }
    fun eaten(value: Int)       { editor.putInt(KEY_EATEN, value) }
    fun spent(value: Int)       { editor.putInt(KEY_SPENT, value) }
    fun clock(value: String)    { editor.putString(KEY_CLOCK, value) }

    fun hasDessert(bundle: Bundle)= bundle.keySet().containsAll(DESERTS_KEYS)

    fun dessert(from: Bundle)  = from.getInt(KEY_DESSERT)
    fun eaten(from: Bundle)    = from.getInt(KEY_EATEN)
    fun spent(from: Bundle)    = from.getInt(KEY_SPENT)
    fun clock(from: Bundle) = from.getString(KEY_CLOCK)

    fun asBundle() = Bundle().apply {
        if (prefs.contains(KEY_DESSERT))  putInt(KEY_DESSERT, prefs.getInt(KEY_DESSERT, 0))
        if (prefs.contains(KEY_EATEN))    putInt(KEY_EATEN, prefs.getInt(KEY_EATEN, 0))
        if (prefs.contains(KEY_SPENT))    putInt(KEY_SPENT, prefs.getInt(KEY_SPENT, 0))
        if (prefs.contains(KEY_CLOCK))    putString(KEY_CLOCK, prefs.getString(KEY_CLOCK, "0"))
    }

}
