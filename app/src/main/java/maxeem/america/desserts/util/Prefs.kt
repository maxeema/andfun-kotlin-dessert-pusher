package maxeem.america.desserts.util

import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import maxeem.america.desserts.app
import org.jetbrains.anko.defaultSharedPreferences

object Prefs : LifecycleObserver {

    private const val KEY_MADE   = "made"
    private const val KEY_EARNED = "earned"
    private const val KEY_TIMER  = "timer"

    private const val KEY_GOT_SWIPE_DESSERT_TIP  = "got_swipe_dessert_tip"

    private val prefs = app.defaultSharedPreferences
    private val editor = prefs.edit()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun save() = editor.apply()

    fun made(value: Int)   { editor.putInt(KEY_MADE, value) }
    fun earned(value: Int) { editor.putInt(KEY_EARNED, value) }
    fun timer(value: Str)  { editor.putString(KEY_TIMER, value) }

    val made   get() = prefs.getInt(KEY_MADE, 0)
    val earned get() = prefs.getInt(KEY_EARNED, 0)
    val timer  get() = prefs.getString(KEY_TIMER, null)

    val hasSwipeDessertTipGot get() = prefs.getBoolean(KEY_GOT_SWIPE_DESSERT_TIP, false)
    fun gotSwipeDessertTip() = prefs.edit { putBoolean(KEY_GOT_SWIPE_DESSERT_TIP, true) }

}
