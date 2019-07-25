package com.example.android.desserts

import android.os.Build.VERSION_CODES.*

data class Dessert
           (val id: Int,        val nameId: Int,             val imageId: Int,       val price: Int)

val desserts = listOf(
    Dessert(CUPCAKE,            R.string.cupcake,            R.drawable.cupcake,          5),
    Dessert(DONUT,              R.string.donut,              R.drawable.donut,            10),
    Dessert(ECLAIR,             R.string.eclair,             R.drawable.eclair,           15),
    Dessert(FROYO,              R.string.froyo,              R.drawable.froyo,            30),
    Dessert(GINGERBREAD,        R.string.gingerbread,        R.drawable.gingerbread,      50),
    Dessert(HONEYCOMB,          R.string.honeycomb,          R.drawable.honeycomb,        75),
    Dessert(ICE_CREAM_SANDWICH, R.string.ice_cream_sandwich, R.drawable.icecreamsandwich, 100),
    Dessert(JELLY_BEAN,         R.string.jelly_bean,         R.drawable.jellybean,        120),
    Dessert(KITKAT,             R.string.kitkat,             R.drawable.kitkat,           140),
    Dessert(LOLLIPOP,           R.string.lollipop,           R.drawable.lollipop,         165),
    Dessert(M,                  R.string.marshmallow,        R.drawable.marshmallow,      185),
    Dessert(N,                  R.string.nougat,             R.drawable.nougat,           200),
    Dessert(O,                  R.string.oreo,               R.drawable.oreo,             222),
    Dessert(P,                  R.string.pie,                R.drawable.pie,              292)
)
