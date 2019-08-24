package maxeem.america.desserts.model

import androidx.lifecycle.*
import maxeem.america.desserts.data.desserts
import maxeem.america.desserts.data.randomDessert
import maxeem.america.desserts.util.Prefs
import maxeem.america.desserts.util.Timer
import maxeem.america.desserts.util.asImmutable
import maxeem.america.desserts.util.asMutable
import java.text.NumberFormat
import java.util.*

class DessertsModel(state: SavedStateHandle) : ViewModel() {

    private companion object {
        const val KEY_TIMER   = "timer"
        const val KEY_MADE    = "made"
        const val KEY_EARNED  = "earned"
        const val KEY_DESSERT = "dessert"

        // always use the same currency and number formatting cuz we support only English localization
        private val LOCALE = Locale.US
        // and of course, show prices in Dollars only now
        private val CURRENCY = Currency.getInstance("USD")
        //
        private val currencyFormat = NumberFormat.getCurrencyInstance(LOCALE).apply {
            setCurrency(CURRENCY)
            maximumFractionDigits = 0
        }
        private val numberFormat = NumberFormat.getNumberInstance(LOCALE)
        private val numberFormatNoGrouping = NumberFormat.getNumberInstance(LOCALE).apply {
            isGroupingUsed = false
        }
    }

    private val timer = Timer(Prefs.timer?.toULong() ?: 0UL) {
        Prefs.timer(valueStr)
        appTime.value = numberFormatNoGrouping.format(value.toLong()) as String? ?: valueStr
    }

    val dessert = state.getLiveData(KEY_DESSERT, randomDessert())
    val made = state.getLiveData(KEY_MADE, Prefs.made)
    val earned = state.getLiveData(KEY_EARNED, Prefs.earned)
    val appTime = state.getLiveData<String>(KEY_TIMER)

    val dessertName  = dessert.map { it.nameId }
    val dessertImage = dessert.map { it.imageId }
    val dessertPrice = dessert.map { currencyFormat.format(it.price) ?: "$${it.price}" }
    val madeAmount   =    made.map { numberFormat.format(it) ?: it.toString() }
    val moneyEarned  =  earned.map { currencyFormat.format(it) ?: "$${it}" }

    val timerLifecycleObserver get() = timer as LifecycleObserver

    val dessertMadeEvent = MutableLiveData<Int?>().asImmutable()
    val dessertSwipeEvent = MutableLiveData<Boolean?>().asImmutable()

    fun handleDessertMadeEvent() { dessertMadeEvent.asMutable().value = null }
    fun handleDessertSwipeEvent() { dessertSwipeEvent.asMutable().value = null }

    fun swipeDessert(next: Boolean) = true.also {
        val idx = desserts.indexOf(requireNotNull(dessert.value))

        dessert.value = when (next) {
            true -> if (idx < desserts.size - 1) desserts[idx + 1] else desserts.first()
            else -> if (idx == 0) desserts.last() else desserts[idx - 1]
        }

        dessertSwipeEvent.asMutable().value = next
    }

    fun madeDessert() {
        made.value = requireNotNull(made.value) + 1
        earned.value = requireNotNull(earned.value) + requireNotNull(dessert.value).price

        Prefs.apply {
            made(requireNotNull(this@DessertsModel.made.value))
            earned(requireNotNull(this@DessertsModel.earned.value))
        }

        dessertMadeEvent.asMutable().value = requireNotNull(made.value)
    }

}