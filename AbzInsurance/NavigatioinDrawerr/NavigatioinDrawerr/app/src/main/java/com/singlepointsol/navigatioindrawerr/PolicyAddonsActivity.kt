package com.singlepointsol.navigatioindrawerr

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.singlepointsol.navigatioindrawerr.databinding.ActivityProductAddonBinding

class PolicyAddonsActivity : AppCompatActivity() {
   /* private lateinit var binding: ActivityProductAddonBinding
    private lateinit var productaddonService:*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy_addons)

        // Initialize the AutoCompleteTextView for Policy Number
        val policyNumber: AutoCompleteTextView = findViewById(R.id.policy_no_et)

        // Initialize the AutoCompleteTextView for Addon ID
        val addonId: AutoCompleteTextView = findViewById(R.id.policyadd_on_id_et)

        // Initialize the AutoCompleteTextView for Payment Mode
        val paymentMode: TextInputEditText = findViewById(R.id.amount_et)

        // Create ArrayAdapter for Policy Number dropdown
        val policyNumberAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.policyNumber,  // The string-array resource containing options
            android.R.layout.simple_dropdown_item_1line // Dropdown item layout
        )

        // Create ArrayAdapter for Addon ID dropdown
        val policyAddonIdAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.policyAddon_Id,  // The string-array resource containing options
            android.R.layout.simple_dropdown_item_1line // Dropdown item layout
        )

        // Create ArrayAdapter for Payment Mode dropdown


        // Set adapters to the AutoCompleteTextViews
        policyNumber.setAdapter(policyNumberAdapter)
        addonId.setAdapter(policyAddonIdAdapter)


        // Optional: Handle item selection events
        policyNumber.setOnItemClickListener { parent, _, position, _ ->
            val selectedPolicyNumber = parent.getItemAtPosition(position).toString()
            println("Selected Policy Number: $selectedPolicyNumber")
        }

        addonId.setOnItemClickListener { parent, _, position, _ ->
            val selectedAddonId = parent.getItemAtPosition(position).toString()
            println("Selected Addon ID: $selectedAddonId")
        }



        // Optional: Add Save Button functionality
        val saveButton: Button = findViewById(R.id.policyaddon_save_button)
        saveButton.setOnClickListener {
            val selectedPolicyNumber = policyNumber.text.toString()
            val selectedAddonId = addonId.text.toString()
            val selectedPaymentMode = paymentMode.text.toString()

            // Perform save logic here
            println("Saving details:")
            println("Policy Number: $selectedPolicyNumber")
            println("Addon ID: $selectedAddonId")
            println("Payment Mode: $selectedPaymentMode")
        }
    }
}
