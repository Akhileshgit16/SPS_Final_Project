package com.singlepointsol.ABZ_Final_Project.ProductAddon

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.singlepointsol.ABZ_Final_Project.MainActivity
import com.singlepointsol.navigatioindrawerr.R


class ProductAddonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the activity layout using the activity_product_addon XML
        setContentView(R.layout.activity_product_addon)
        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Optionally, you can enable the back button on the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val productidEditText:AutoCompleteTextView=findViewById(R.id.productId_dropdown)
        val productIdArray = arrayOf("show")
        val productIdOptionsAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            productIdArray
        )
        productidEditText.setAdapter(productIdOptionsAdapter)
        //addonid
        val productaddonIdEditText:AutoCompleteTextView=findViewById(R.id.addonId_dropdown)
        val productaddonidarray= arrayOf("show")
        val productidAdapter= ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            productaddonidarray
            )
        productaddonIdEditText.setAdapter(productidAdapter)
    }


    // Override to handle toolbar back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()  // Navigate back to the main activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val backIntent = Intent(this, MainActivity::class.java)
        startActivity(backIntent)
        finish() // Close the current activity
    }

    }

