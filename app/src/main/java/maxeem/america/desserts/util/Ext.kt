package maxeem.america.desserts.util

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import maxeem.america.desserts.app

/**
 * Extensions
 */

val Any.hash get() = hashCode()

fun Int.asString() = app.getString(this)
fun String.fromHtml() = Util.fromHtml(this)

fun View.onClick(l: ()->Unit) = setOnClickListener { l() }

fun Fragment.compatActivity() = activity as AppCompatActivity?

fun AppCompatActivity.delayed(delay: Long, stateAtLeast: Lifecycle.State = Lifecycle.State.CREATED, code: ()->Unit) {
    if (isFinishing || isDestroyed) return
    app.handler.postDelayed(delay) {
        if (lifecycle.currentState.isAtLeast(stateAtLeast))
            code()
    }
}

fun <T> MutableLiveData<T>.asImmutable() = this as LiveData<T>
fun <T> LiveData<T>.asMutable()          = this as MutableLiveData<T>
