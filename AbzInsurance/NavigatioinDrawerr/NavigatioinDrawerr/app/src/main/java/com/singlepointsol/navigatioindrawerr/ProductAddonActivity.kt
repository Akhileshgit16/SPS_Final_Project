package com.singlepointsol.navigatioindrawerr

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity

class ProductAddonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the activity layout using the activity_product_addon XML
        setContentView(R.layout.activity_product_addon)
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
    }

