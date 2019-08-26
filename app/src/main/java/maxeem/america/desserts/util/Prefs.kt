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

    private const val KEY_TIP_PREFIX = "tip"

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

    fun hasTipGotten(tip: Tips) = prefs.getBoolean("${KEY_TIP_PREFIX}_${tip.id}", false)
    fun setTipGotten(tip: Tips) = prefs.edit { putBoolean("${KEY_TIP_PREFIX}_${tip.id}", true) }

}
