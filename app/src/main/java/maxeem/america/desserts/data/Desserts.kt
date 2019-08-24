package maxeem.america.desserts.data

import android.os.Build.VERSION_CODES.*
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import maxeem.america.desserts.R
import kotlin.random.Random

@Parcelize
data class Dessert(val id: Int,
                   val nameId: Int,
                   val imageId: Int,
                   val price: Int = id) : Parcelable

val desserts = listOf(
        Dessert(CUPCAKE, R.string.cupcake, R.drawable.cupcake),
        Dessert(DONUT, R.string.donut, R.drawable.donut),
        Dessert(ECLAIR, R.string.eclair, R.drawable.eclair),
        Dessert(FROYO, R.string.froyo, R.drawable.froyo),
        Dessert(GINGERBREAD, R.string.gingerbread, R.drawable.gingerbread),
        Dessert(HONEYCOMB, R.string.honeycomb, R.drawable.honeycomb),
        Dessert(ICE_CREAM_SANDWICH, R.string.ice_cream_sandwich, R.drawable.icecreamsandwich),
        Dessert(JELLY_BEAN, R.string.jelly_bean, R.drawable.jellybean),
        Dessert(KITKAT, R.string.kitkat, R.drawable.kitkat),
        Dessert(LOLLIPOP, R.string.lollipop, R.drawable.lollipop),
        Dessert(M, R.string.marshmallow, R.drawable.marshmallow),
        Dessert(N, R.string.nougat, R.drawable.nougat),
        Dessert(O, R.string.oreo, R.drawable.oreo),
        Dessert(P, R.string.pie, R.drawable.pie)
)

fun randomDessert(not: Dessert? = null) : Dessert {
    while(true) {
        val des = desserts[Random.nextInt(desserts.size)]
        if (des != not) return des
    }
}

fun dessertByPrice(price: Int) = desserts.first { it.price == price }
