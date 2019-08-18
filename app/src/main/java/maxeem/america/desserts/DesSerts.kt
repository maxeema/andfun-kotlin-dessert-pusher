package maxeem.america.desserts

import android.os.Build.VERSION_CODES.*
import kotlin.random.Random

data class Dessert
           (val id: Int,        val nameId: Int,      val imageId: Int,       val price: Int)

val desserts = listOf(
    Dessert(CUPCAKE,            R.string.cupcake,            R.drawable.cupcake,           15),
    Dessert(DONUT,              R.string.donut,              R.drawable.donut,              5),
    Dessert(ECLAIR,             R.string.eclair,             R.drawable.eclair,             7),
    Dessert(FROYO,              R.string.froyo,              R.drawable.froyo,              5),
    Dessert(GINGERBREAD,        R.string.gingerbread,        R.drawable.gingerbread,        3),
    Dessert(HONEYCOMB,          R.string.honeycomb,          R.drawable.honeycomb,         20),
    Dessert(ICE_CREAM_SANDWICH, R.string.ice_cream_sandwich,          R.drawable.icecreamsandwich,   6),
    Dessert(JELLY_BEAN,         R.string.jelly_bean,         R.drawable.jellybean,          4),
    Dessert(KITKAT,             R.string.kitkat,             R.drawable.kitkat,             2),
    Dessert(LOLLIPOP,           R.string.lollipop,           R.drawable.lollipop,           1),
    Dessert(M,                  R.string.marshmallow,        R.drawable.marshmallow,        2),
    Dessert(N,                  R.string.nougat,             R.drawable.nougat,             4),
    Dessert(O,                  R.string.oreo,               R.drawable.oreo,               2),
    Dessert(P,                  R.string.pie,                R.drawable.pie,                8)
)

fun randomDessert(not: Dessert? = null) : Dessert {
    while(true) {
        val des = desserts[Random.nextInt(desserts.size)]
        if (des != not) return des
    }
}
