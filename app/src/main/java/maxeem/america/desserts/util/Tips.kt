package maxeem.america.desserts.util

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import maxeem.america.desserts.R
import maxeem.america.desserts.app
import org.jetbrains.anko.design.indefiniteSnackbar
import java.lang.ref.WeakReference

enum class Tips(val id: Str, @StringRes val nameRes: Int) {

    SWIPE_DESSERT("swipe-dessert", R.string.tip_swipe_dessert);

    fun consume() {
        if (isConsumed) return
        hide(this); Prefs.setTipGotten(this)
    }
    val isConsumed
        get() = Prefs.hasTipGotten(this)

    fun show(lifecycleOwner: LifecycleOwner, view: View, anchor: View) {
        if (!isConsumed)
            show(this, lifecycleOwner, view, anchor)
    }
    fun hide() = hide(this)

    private companion object {
        private const val POPUP_DELAY  = 3_500L
        private const val POPUP_LENGTH = 7_000L

        private val weakHashMap = mutableMapOf<Tips, WeakReference<Snackbar>>()

        private fun show(tip: Tips, lifecycleOwner: LifecycleOwner, view: View, anchor: View) {
            lifecycleOwner.delayed(POPUP_DELAY, tip.id, Lifecycle.State.RESUMED) {
                if (tip.isConsumed) return@delayed
                view.indefiniteSnackbar(tip.nameRes, R.string.got_it) {
                    tip.consume()
                }.apply {
                    weakHashMap.put(tip, WeakReference(this))
                    anchorView = anchor
                    addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_SWIPE)
                                tip.consume()
                        }
                    })
                    lifecycleOwner.delayed(POPUP_LENGTH, tip.id, Lifecycle.State.RESUMED) { dismiss() }
                }
            }
        }
        private fun hide(tip: Tips) {
            app.handler.removeCallbacksAndMessages(tip.id)
            weakHashMap.remove(tip)?.get()?.dismiss()
        }
    }

}
