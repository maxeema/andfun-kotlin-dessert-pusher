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

package maxeem.america.desserts

import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_main.*
import maxeem.america.desserts.databinding.ActivityMainBinding
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.longToast
import org.jetbrains.anko.px2dip
import timber.log.Timber
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.atan2

class DesActivity : AppCompatActivity(), AnkoLogger {

    private var          des   = randomDessert()
    private var          spent = 0
    private var          eaten = 0
    private lateinit var time  : DesTime

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate, savedInstanceState: $savedInstanceState")
        supportActionBar?.title = getString(R.string.app_title)

        // Use Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.da = Data(this)

        // Initialize trying to restore
        (savedInstanceState ?: Persistent.asBundle()).apply {
            info("  init trying to restore : $this")
            eaten = Persistent.eaten(this)
            spent = Persistent.spent(this)
            time  = DesTime(Persistent.clock(this)?.toULong()) {
                binding.invalidateAll()
                Persistent.clock(valueStr)
            }
        }

        // Register Lifecycle observers
        lifecycle.addObserver(time)
        lifecycle.addObserver(Persistent)

        // Handle swipe next/prev events
        dessertImage.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        dessertImage.setImageResource(des.imageId)
    }

    override fun onRestart() {
        super.onRestart()
        info("onRestart")
        // Show random dessert on every app comeback
        des = randomDessert(des)
        binding.invalidateAll()
        (dessertImage.currentView as ImageView).setImageResource(des.imageId)
        fadeInAnimation.apply {
            dessertImage.currentView.animation = this
            dessertName.animation = this
            start()
        }
    }
    override fun onStart() {
        super.onStart()
        info("onStart, lifecycle.currentState; ${lifecycle.currentState}")
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        info("onRestoreInstanceState, savedInstanceState: $savedInstanceState")
    }
    override fun onResume() {
        super.onResume()
        info("onResume, lifecycle.currentState; ${lifecycle.currentState}")
    }
    override fun onPostResume() {
        super.onPostResume()
        info("onPostResume, lifecycle.currentState; ${lifecycle.currentState}")
    }
    override fun onPause() {
        super.onPause()
        info("onPause, lifecycle.currentState; ${lifecycle.currentState}")
    }
    override fun onStop() {
        super.onStop()
        info("onStop, isFinishing: lifecycle.currentState; ${lifecycle.currentState}")
        // Free MediaPlayers resources
        mpEat  = null
        mpNext = null
        mpPrev = null
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        info("onSaveInstanceState")
        outState.putAll(Persistent.asBundle())
    }
    override fun onDestroy() {
        super.onDestroy()
        info("onDestroy")
    }

    /**
     * Data for bound views
     */
    class Data(private val a: DesActivity) {
        private companion object {
            // always use the same currency and number formatting cuz we support only English localization
            private val LOCALE = Locale.US
            // and of course, show prices in Dollars only now
            private val CURRENCY = Currency.getInstance("USD")
        }
        private val currencyFormat = NumberFormat.getCurrencyInstance(LOCALE).apply {
            setCurrency(CURRENCY)
            maximumFractionDigits = 0
        }
        private val numberFormat = NumberFormat.getNumberInstance(LOCALE)
        private val numberFormatNoGrouping = NumberFormat.getNumberInstance(LOCALE).apply {
            isGroupingUsed = false
        }
        val name  get() = a.des.nameId
        val price get() = currencyFormat.format(a.des.price) ?: "$${a.des.price}"
        val eaten get() = numberFormat.format(a.eaten) ?: a.eaten.toString()
        val spent get() = currencyFormat.format(a.spent) ?: "$${a.spent}"
        val clock get() = numberFormatNoGrouping.format(a.time.value.toLong()) ?: a.time.valueStr
    }

    /**
     * Menu functional
     */
    private var shareMenuItem : MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        if (eaten == 0) menu.findItem(R.id.shareMenuButton).apply {
            isEnabled = false // don't allow share when nothing eaten
            shareMenuItem = this
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.shareMenuButton -> onShare()
        else -> super.onOptionsItemSelected(item)
    }

    private fun onShare() = with (ShareCompat.IntentBuilder.from(this)) {
        setText(getString(R.string.share_text, binding.da!!.eaten, binding.da!!.spent, getString(R.string.app_name)))
        setType("text/plain")
        intent.runCatching {
            startActivity(this)
        }.map { true }.onFailure { longToast(R.string.sharing_not_available) }.getOrDefault(false)
    }

    /**
     * Desserts functional
     */
    fun eatDessert(view: View) {
        spent += des.price
        eaten++

        Persistent.apply {
            spent(spent)
            eaten(eaten)
        }

        fadeInAnimation.apply {
            spentMoney.animation = this
            eatenAmount.animation = this
            start()
        }

        dessertImage.inAnimation = newDessertAnimation
        dessertImage.outAnimation = eatDessertAnimation
        dessertImage.setImageResource(des.imageId)

        mpEat!!.apply {
            seekTo(0)
            start()
        }

        binding.invalidateAll()

        shareMenuItem?.takeIf { eaten == 1 }?.apply {
            isEnabled = true
        }
    }

    private fun swipeDessert(next: Boolean) = true.also {
        val idx = desserts.indexOf(des)
        des = when(next) {
            true -> if (idx < desserts.size-1) desserts[idx+1] else desserts.first()
            else -> if (idx == 0) desserts.last() else desserts[idx-1]
        }

        dessertImage.inAnimation = if (next) nextDessertAnimation else prevDessertAnimation
        dessertImage.outAnimation = if (next) nextOutDessertAnimation else prevOutDessertAnimation
        dessertImage.setImageResource(des.imageId)

        fadeInAnimation.apply {
            dessertName.animation = this
            dessertPrice.animation = this
            start()
        }

        (if (next) mpNext else mpPrev)!!.apply {
            seekTo(0)
            start()
        }

        binding.invalidateAll()
    }

    /**
     * MediaPlayers
     */
    private var mpEat : MediaPlayer? = null
        get() = field ?: MediaPlayer.create(this, R.raw.eat).apply { field = this }
        set(value) { if (value == null) field?.release(); field = value }
    private var mpNext : MediaPlayer? = null
        get() = field ?: MediaPlayer.create(this, R.raw.sweet_next).apply { field = this }
        set(value) { if (value == null) field?.release(); field = value }
    private var mpPrev : MediaPlayer? = null
        get() = field ?: MediaPlayer.create(this, R.raw.sweet_prev).apply { field = this }
        set(value) { if (value == null) field?.release(); field = value }

    /**
     * Animations
     */
    private val fadeInAnimation         by lazy { AnimationUtils.loadAnimation(this, android.R.anim.fade_in) }

    private val newDessertAnimation     by lazy { AnimationUtils.loadAnimation(this, R.anim.sweet_new) }
    private val eatDessertAnimation     by lazy { AnimationUtils.loadAnimation(this, R.anim.sweet_eat) }
    private val prevDessertAnimation    by lazy { AnimationUtils.loadAnimation(this, R.anim.sweet_prev) }
    private val nextDessertAnimation    by lazy { AnimationUtils.loadAnimation(this, R.anim.sweet_next) }
    private val prevOutDessertAnimation by lazy { AnimationUtils.loadAnimation(this, R.anim.sweet_prev_out) }
    private val nextOutDessertAnimation by lazy { AnimationUtils.loadAnimation(this, R.anim.sweet_next_out) }

    /**
     * Swiping
     */
    private val gestureDetector : GestureDetectorCompat by lazy { GestureDetectorCompat(this, object: GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocity1: Float, velocity2: Float) : Boolean {
            if (e1 == null || e2 == null) return false
            if (px2dip(abs(e1.x-e2.x).toInt()) < 25) return false
            return when (detect(Math.toDegrees(atan2(e1.y.toDouble() - e2.y.toDouble(), e2.x.toDouble() - e1.x.toDouble())).toInt())) {
                MotionEvent.EDGE_LEFT -> swipeDessert(true)
                MotionEvent.EDGE_RIGHT -> swipeDessert(false)
                else -> true
            }
        }
        private fun detect(angle: Int) = when (angle) {
            /*top*/    in 45..135                        -> MotionEvent.EDGE_TOP
            /*bottom*/ in -45 downTo -135                -> MotionEvent.EDGE_BOTTOM
            /*left*/   in 135..180, in -135 downTo -180  -> MotionEvent.EDGE_LEFT
            /*right*/  in -45..45                        -> MotionEvent.EDGE_RIGHT
            else -> Int.MIN_VALUE
        }
    })}

}
