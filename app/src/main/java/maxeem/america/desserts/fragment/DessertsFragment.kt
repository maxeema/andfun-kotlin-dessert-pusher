package maxeem.america.desserts.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.core.app.ShareCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import maxeem.america.desserts.R
import maxeem.america.desserts.app
import maxeem.america.desserts.data.dessertByPrice
import maxeem.america.desserts.databinding.FragmentDessertsBinding
import maxeem.america.desserts.model.DessertsModel
import maxeem.america.desserts.util.Bool
import maxeem.america.desserts.util.Prefs
import maxeem.america.desserts.util.asString
import maxeem.america.desserts.util.delayed
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.indefiniteSnackbar
import org.jetbrains.anko.info
import org.jetbrains.anko.longToast
import org.jetbrains.anko.px2dip
import kotlin.math.abs
import kotlin.math.atan2

class DessertsFragment : Fragment(), AnkoLogger {

    private companion object {
        private const val swipeDessertTipToken = "swipeDessertTipToken"
    }

    private val model : DessertsModel by viewModels { SavedStateViewModelFactory(app, this) }
    private lateinit var binding: FragmentDessertsBinding

    private var snackbarTipSwipeDessert : Snackbar? = null

    private val des get() = requireNotNull(model.dessert.value)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = FragmentDessertsBinding.inflate(inflater, container, false)

        binding.model = model
        binding.lifecycleOwner = viewLifecycleOwner

        // Handle swipe next/prev events
        binding.dessertImg.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        model.dessertMadeEvent.observe(viewLifecycleOwner) {
            it?.also { onDessertMade(it); model.handleDessertMadeEvent() }
        }
        model.dessertSwipeEvent.observe(viewLifecycleOwner) {
            it?.also { onDessertSwiped(it); model.handleDessertSwipeEvent() }
        }

        viewLifecycleOwner.lifecycle.addObserver(model.timerLifecycleObserver)

        return binding.root
    }

    override fun onStop() { super.onStop()
        info("onStop, isFinishing: lifecycle.currentState; ${lifecycle.currentState}")
        // Free MediaPlayers resources
        mpMake  = null
        mpNext = null
        mpPrev = null
    }

    override fun onResume() { super.onResume()
        if (!Prefs.hasSwipeDessertTipGot)
            viewLifecycleOwner.delayed(3500, swipeDessertTipToken, Lifecycle.State.RESUMED) {
                if (Prefs.hasSwipeDessertTipGot) return@delayed
                snackbarTipSwipeDessert = view!!.indefiniteSnackbar(R.string.tip_swipe_dessert, R.string.got_it) { Prefs.gotSwipeDessertTip() }
                snackbarTipSwipeDessert!!.animationMode
                snackbarTipSwipeDessert!!.addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == DISMISS_EVENT_SWIPE) Prefs.gotSwipeDessertTip()
                    }
                })
                viewLifecycleOwner.delayed(7000, swipeDessertTipToken, Lifecycle.State.RESUMED) { snackbarTipSwipeDessert?.dismiss() }
            }
    }
    override fun onPause() { super.onPause()
        snackbarTipSwipeDessert?.dismiss()
        app.handler.removeCallbacksAndMessages(swipeDessertTipToken)
    }

    /**
     * Menu functional
     */
    private var shareMenuItem : MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        if (model.made.value!! == 0) menu.findItem(R.id.shareMenuItem).apply {
            this.isEnabled = false
            shareMenuItem = this
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.shareMenuItem -> onShare()
        R.id.aboutFragment -> NavigationUI.onNavDestinationSelected(item, findNavController())
        else -> super.onOptionsItemSelected(item)
    }

    private fun onShare() = with (ShareCompat.IntentBuilder.from(activity!!)) {
        setText(when(model.madeAmount.value!!.toInt()) {
            1 -> getString(R.string.share_text_one, dessertByPrice(model.earned.value!!).nameId.asString(),
                    model.moneyEarned.value, getString(R.string.app_name))
            else -> getString(R.string.share_text_many, model.madeAmount.value, model.moneyEarned.value, getString(R.string.app_name))
        })
        setType("text/plain")
        intent.runCatching {
            startActivity(this)
        }.map { true }.onFailure { activity?.longToast(R.string.sharing_not_available) }.getOrDefault(false)
    }

    /**
     * Desserts functional
     */
    private fun onDessertMade(amount: Int) {
        fadeInAnimation.apply {
            binding.moneySpent.animation = this
            binding.earnedAmount.animation = this
            start()
        }

        binding.dessertImg.inAnimation = newDessertAnimation
        binding.dessertImg.outAnimation = madeDessertAnimation
        binding.dessertImg.setImageResource(des.imageId)

        mpMake!!.apply {
            seekTo(0)
            start()
        }

        shareMenuItem?.takeIf { amount == 1 }?.apply {
            this.isEnabled = true
        }

        snackbarTipSwipeDessert?.dismiss()
    }

    private fun onDessertSwiped(next: Bool) {
        binding.dessertImg.inAnimation = if (next) nextDessertAnimation else prevDessertAnimation
        binding.dessertImg.outAnimation = if (next) nextOutDessertAnimation else prevOutDessertAnimation

        fadeInAnimation.apply {
            binding.dessertName.animation = this
            binding.dessertPrice.animation = this
            start()
        }

        (if (next) mpNext else mpPrev)!!.apply {
            seekTo(0)
            start()
        }

        if (!Prefs.hasSwipeDessertTipGot) {
            Prefs.gotSwipeDessertTip()
            snackbarTipSwipeDessert?.dismiss()
        }
    }

    /**
     * MediaPlayers
     */
    private var mpMake : MediaPlayer? = null
        get() = field ?: MediaPlayer.create(context!!, R.raw.make).apply { field = this }
        set(value) { if (value == null) field?.release(); field = value }
    private var mpNext : MediaPlayer? = null
        get() = field ?: MediaPlayer.create(context!!, R.raw.sweet_next).apply { field = this }
        set(value) { if (value == null) field?.release(); field = value }
    private var mpPrev : MediaPlayer? = null
        get() = field ?: MediaPlayer.create(context!!, R.raw.sweet_prev).apply { field = this }
        set(value) { if (value == null) field?.release(); field = value }

    /**
     * Animations
     */
    private val fadeInAnimation         by lazy { AnimationUtils.loadAnimation(context!!, android.R.anim.fade_in) }

    private val newDessertAnimation     by lazy { AnimationUtils.loadAnimation(context!!, R.anim.sweet_new) }
    private val madeDessertAnimation     by lazy { AnimationUtils.loadAnimation(context!!, R.anim.sweet_make) }
    private val prevDessertAnimation    by lazy { AnimationUtils.loadAnimation(context!!, R.anim.sweet_prev) }
    private val nextDessertAnimation    by lazy { AnimationUtils.loadAnimation(context!!, R.anim.sweet_next) }
    private val prevOutDessertAnimation by lazy { AnimationUtils.loadAnimation(context!!, R.anim.sweet_prev_out) }
    private val nextOutDessertAnimation by lazy { AnimationUtils.loadAnimation(context!!, R.anim.sweet_next_out) }

    /**
     * Swiping
     */
    private val gestureDetector : GestureDetectorCompat by lazy { GestureDetectorCompat(context!!, object: GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocity1: Float, velocity2: Float) : Bool {
            if (e1 == null || e2 == null) return false
            if (app.px2dip(abs(e1.x-e2.x).toInt()) < 25) return false
            return when (detect(Math.toDegrees(atan2(e1.y.toDouble() - e2.y.toDouble(), e2.x.toDouble() - e1.x.toDouble())).toInt())) {
                MotionEvent.EDGE_LEFT  -> model.swipeDessert(true)
                MotionEvent.EDGE_RIGHT -> model.swipeDessert(false)
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