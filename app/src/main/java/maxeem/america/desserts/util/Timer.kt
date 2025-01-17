/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package maxeem.america.desserts.util

import android.os.Handler
import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class Timer(initial: ULong? = null,
            @IntRange(from= MIN_INTERVAL.toLong()) private val interval: UInt = DEF_INTERVAL.toUInt(),
            tikTak: Timer.()-> Unit)
    : LifecycleObserver {

    private companion object {
        private const val MIN_INTERVAL = 500
        private const val DEF_INTERVAL = 1000
    }

    init { require(interval >= MIN_INTERVAL.toUInt()) }

    // Current value
    val value       get() = counter
    val valueStr    get() = counter.toString()
    // Still counting ?..
    val isActive    get() = active

    // Implementation
    private var active = false
    private var counter = initial ?: ULong.MIN_VALUE
    private val handler : Handler by lazy { Handler() }
    private val runnable : ()->Unit = { counter++; this.tikTak(); post() }

    private fun post() = handler.postDelayed(runnable, interval.toLong())

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() = active || { active = true; post(); true } ()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() = !active || { active = false; handler.removeCallbacksAndMessages(null); true } ()

    override fun toString() = "Dessert Timer has '$counter' (${if (active) "still active" else "already stopped"})"

}
