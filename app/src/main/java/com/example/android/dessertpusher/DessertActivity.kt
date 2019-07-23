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

package com.example.android.dessertpusher

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.example.android.dessertpusher.databinding.ActivityMainBinding
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import timber.log.Timber

class DessertActivity : AppCompatActivity(), AnkoLogger {

    private var revenue = 0
    private var dessertsSold = 0

    private lateinit var binding: ActivityMainBinding
    private lateinit var currentDessert : Dessert
    private lateinit var timer : DessertTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate, savedInstanceState: $savedInstanceState")

        //Restore or initialize state
        currentDessert = savedInstanceState?.takeIf { it.containsKey("dessert") }?.run {
            val id = getInt("dessert")
            Desserts.all.find { id == it.id }?.also {
                dessertsSold = getInt("sold")
                revenue = getInt("revenue")
            }
        } ?: Desserts.first

        // Init timer
        DessertTimer(savedInstanceState?.getString("timer")?.toULong()) {
            Timber.i("timer is at: $it")
            binding.timer = it.toString()
        }.apply {
            timer = this
            lifecycle.addObserver(this)
        }

        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.dessertButton.setOnClickListener(::onDessertClicked)

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imageId)

        // Set up timer
        binding.timer = timer.valueStr

    }
    override fun onRestart() {
        super.onRestart()
        info("onRestart")
    }
    override fun onStart() {
        super.onStart()
//        timer.start()
        Timber.i("onStart called")
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        info("onRestoreInstanceState, savedInstanceState: $savedInstanceState")
    }
    override fun onResume() {
        super.onResume()
        info("onResume")
    }
    override fun onPause() {
        super.onPause()
        info("onPause")
    }
    override fun onStop() {
        super.onStop()
//        timer.stop()
        info("onStop")
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        info("onSaveInstanceState")
        outState.putInt("dessert", currentDessert.id)
        outState.putInt("sold", dessertsSold)
        outState.putInt("revenue", revenue)
        outState.putString("timer", timer.valueStr)
    }
    override fun onDestroy() {
        super.onDestroy()
        info("onDestroy")
    }
    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onDessertClicked(view: View) {
        // Update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = Desserts.first
        for (dessert in Desserts.all) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
                .setText(getString(R.string.share_text, dessertsSold, revenue))
                .setType("text/plain")
                .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }

}
