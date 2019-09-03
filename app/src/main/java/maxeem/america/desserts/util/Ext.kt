package maxeem.america.desserts.util

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import maxeem.america.desserts.app

/**
 * Extensions
 */

val Any.hash get() = hashCode()

fun Int.asString() = app.getString(this)
fun Int.asString(vararg args: Any) = app.getString(this, *args)
fun String.fromHtml() = Util.fromHtml(this)

fun View.onClick(l: ()->Unit) = setOnClickListener { l() }
fun Fragment.compatActivity() = activity as AppCompatActivity?

fun <T> MutableLiveData<T>.asImmutable() = this as LiveData<T>
fun <T> LiveData<T>.asMutable()          = this as MutableLiveData<T>

fun LifecycleOwner.delayed(delay: Long, token: Any? = null, stateAtLeast: Lifecycle.State = Lifecycle.State.CREATED, code: ()->Unit) {
    app.handler.postDelayed(delay, token) {
        if (lifecycle.currentState.isAtLeast(stateAtLeast))
            code()
    }
}
fun Fragment.delayed(delay: Long, token: Any? = null, stateAtLeast: Lifecycle.State = Lifecycle.State.CREATED, code: ()->Unit) {
    app.handler.postDelayed(delay, token) {
        if (!(isDetached || isRemoving) && lifecycle.currentState.isAtLeast(stateAtLeast))
            code()
    }
}
fun AppCompatActivity.delayed(delay: Long, token: Any? = null, stateAtLeast: Lifecycle.State = Lifecycle.State.CREATED, code: ()->Unit) {
    app.handler.postDelayed(delay, token) {
        if (!(isFinishing || isDestroyed) && lifecycle.currentState.isAtLeast(stateAtLeast))
            code()
    }
}
