package com.example.android.dessertpusher

import android.os.Build.VERSION_CODES.*

data class Dessert(val id: Int, val imageId: Int, val price: Int, val startProductionAmount: Int)

val desserts = listOf(
    Dessert(CUPCAKE, R.drawable.cupcake, 5, 0),
    Dessert(DONUT, R.drawable.donut, 10, 5),
    Dessert(ECLAIR, R.drawable.eclair, 15, 20),
    Dessert(FROYO, R.drawable.froyo, 30, 50),
    Dessert(GINGERBREAD, R.drawable.gingerbread, 50, 100),
    Dessert(HONEYCOMB, R.drawable.honeycomb, 100, 200),
    Dessert(ICE_CREAM_SANDWICH, R.drawable.icecreamsandwich, 500, 500),
    Dessert(JELLY_BEAN, R.drawable.jellybean, 1000, 1000),
    Dessert(KITKAT, R.drawable.kitkat, 2000, 2000),
    Dessert(LOLLIPOP, R.drawable.lollipop, 3000, 4000),
    Dessert(M, R.drawable.marshmallow, 4000, 8000),
    Dessert(N, R.drawable.nougat, 5000, 16000),
    Dessert(O, R.drawable.oreo, 6000, 20000)
)
