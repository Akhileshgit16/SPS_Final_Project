package com.singlepointsol.navigatioindrawerr.PolicyAddon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.MainActivity
import com.singlepointsol.navigatioindrawerr.databinding.ActivityPolicyAddonsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PolicyAddonsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPolicyAddonsBinding
    private val policyAddonService =
        PolicyAdddonInstance.getInstance().create(PolicyAddonApiService::class.java)

    private var isFirstFetchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyAddonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@PolicyAddonsActivity, MainActivity::class.java)
                startActivity(backIntent)
            }
        })

        // Set listeners for buttons
        binding.policyaddonGetButton.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchPolicyAddOn()
                isFirstFetchDone = true
            } else {
                clearInputFields()
            }
        }
        binding.policyaddonSaveButton.setOnClickListener {
            savePolicyAddOn()
        }
        binding.policyaddonUpdateButton.setOnClickListener {
            updatePolicyAddOn()
        }
        binding.policyaddondeleteButton.setOnClickListener {
            deletePolicyAddOn()
        }

        // Populate dropdown menus
        populateDropdownMenus()
    }

    private fun setupAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        data: List<String?>
    ) {
        val filteredData = data.filterNotNull() // Remove null entries
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            filteredData
        )
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun populateDropdownMenus() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch policy addon details from the server
                val response = policyAddonService.fetchPolicyAddonDetails()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val policyAddonList = response.body()
                        Log.d("PolicyAddonsActivity", "Fetched Policy Addons: $policyAddonList")

                        if (!policyAddonList.isNullOrEmpty()) {
                            // Extract and log addon IDs
                            val addonIds = policyAddonList.mapNotNull { it.addonID }.distinct()
                            Log.d("PolicyAddonsActivity", "Addon IDs: $addonIds")

                            val policyNos = policyAddonList.mapNotNull { it.policyNo }.distinct()
                            Log.d("PolicyAddonsActivity", "Policy Numbers: $policyNos")

                            if (addonIds.isEmpty()) {
                                // Handle case when addonIDs are not available
                                Toast.makeText(
                                    this@PolicyAddonsActivity,
                                    "No addon IDs found. Please try again later.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Populate the dropdowns
                                setupAutoCompleteTextView(binding.addonIdEt, addonIds)
                            }

                            if (policyNos.isEmpty()) {
                                // Handle case when policy numbers are not available
                                Toast.makeText(
                                    this@PolicyAddonsActivity,
                                    "No policy numbers found. Please try again later.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                setupAutoCompleteTextView(binding.policyNoEt, policyNos)
                            }
                        } else {
                            Log.w("PolicyAddonsActivity", "No Policy Addon data found in API response.")
                            Toast.makeText(
                                this@PolicyAddonsActivity,
                                "No Policy Addon data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("PolicyAddonsActivity", "API Response Failed: ${response.message()}")
                        Toast.makeText(
                            this@PolicyAddonsActivity,
                            "Failed to fetch Policy Addon data! Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PolicyAddonsActivity", "Error fetching dropdown data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PolicyAddonsActivity,
                        "Error fetching Policy Addon data! Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun clearInputFields() {
        binding.addonIdEt.text?.clear()
        binding.policyNoEt.text?.clear()
        binding.amountEt.text?.clear()
    }

    private fun getPolicyAddonFromInput(): PolicyAddonItem? {
        val addonId = binding.addonIdEt.text.toString()
        val policyNumber = binding.policyNoEt.text.toString()
        val amount = binding.amountEt.text.toString()

        return if (addonId.isNotEmpty() && policyNumber.isNotEmpty() && amount.isNotEmpty()) {
            PolicyAddonItem(addonID = addonId, policyNo = policyNumber, amount = amount)
        } else null
    }

    private fun fetchPolicyAddOn() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = policyAddonService.fetchPolicyAddonDetails()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val policyAddonList = response.body()
                        if (!policyAddonList.isNullOrEmpty()) {
                            val firstPolicyAddon = policyAddonList.first()
                            binding.addonIdEt.setText(firstPolicyAddon.addonID)
                            binding.policyNoEt.setText(firstPolicyAddon.policyNo)
                            binding.amountEt.setText(firstPolicyAddon.amount)

                            Toast.makeText(
                                this@PolicyAddonsActivity,
                                "Fetched Policy Addon successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@PolicyAddonsActivity,
                                "No Policy Addon data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@PolicyAddonsActivity,
                            "Error fetching Policy Addon: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PolicyAddonsActivity", "Error fetching Policy Addon: ${e.message}", e)
            }
        }
    }

    private fun savePolicyAddOn() {
        val newPolicyAddon = getPolicyAddonFromInput()

        if (newPolicyAddon == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log the final URL to verify it's correct
                Log.d("PolicyAddonsActivity", "Calling API with policyNo: ${newPolicyAddon.policyNo}")

                val response = policyAddonService.addPolicyAddonDetails(
                    policyNo = newPolicyAddon.policyNo,
                    policy = newPolicyAddon
                )
                // Handle the response on the main thread
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@PolicyAddonsActivity,
                            "Policy Addon saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearInputFields()
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(
                            this@PolicyAddonsActivity,
                            "Failed to save Policy Addon: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions like network errors
                Log.e("PolicyAddonsActivity", "Error saving Policy Addon: ${e.message}", e)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PolicyAddonsActivity,
                        "Error saving Policy Addon: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updatePolicyAddOn() {
        // Extract input values
        val policyNo = binding.policyNoEt.text.toString().trim()       // Existing Policy Number
        val newAddonId = binding.addonIdEt.text.toString().trim()      // New Addon ID
        val newAmount = binding.amountEt.text.toString().trim()        // New Amount

        // Validation
        if (policyNo.isEmpty() || newAddonId.isEmpty() || newAmount.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        // Parse amount as Double
        val amount = newAmount.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Invalid amount entered.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the payload for updating the policy addon
        val updatedPolicyAddon = PolicyAddonItem(
            policyNo = policyNo,
            addonID = newAddonId,
            amount = amount.toDouble().toString()
        )

        // Logging for debugging
        Log.d("UpdatePolicyAddon", "Request Payload: $updatedPolicyAddon")

        // Perform API call in background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // API Call
                val response = policyAddonService.updatePolicyAddonDetails(policyNo, updatedPolicyAddon)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Success case
                        Toast.makeText(this@PolicyAddonsActivity, "Policy Addon updated successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        // Failure case
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(this@PolicyAddonsActivity, "Failed to update Policy Addon: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdatePolicyAddon", "Exception during update: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PolicyAddonsActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deletePolicyAddOn() {
        val policyNo = binding.policyNoEt.text.toString().trim()
        val addonID = binding.addonIdEt.text.toString().trim()

        if (policyNo.isEmpty() || addonID.isEmpty()) {
            Toast.makeText(this, "Please enter both policy number and addon ID to delete", Toast.LENGTH_SHORT).show()
            return
        }

        // Confirmation dialog before sending the delete request
        AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete the policy addon with PolicyNo: $policyNo and AddonID: $addonID?")
            .setPositiveButton("Delete") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Make the DELETE request
                        val response = policyAddonService.deletePolicyDetails(policyNo, addonID)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@PolicyAddonsActivity,
                                    "Policy Addon deleted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearInputFields()
                            } else {
                                when (response.code()) {
                                    400 -> {
                                        Toast.makeText(
                                            this@PolicyAddonsActivity,
                                            "Failed to delete Policy Addon: The provided addonID does not match the given policy number.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    404 -> {
                                        Toast.makeText(
                                            this@PolicyAddonsActivity,
                                            "Failed to delete Policy Addon: Either the policy number or addon ID does not exist.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    else -> {
                                        val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                                        Toast.makeText(
                                            this@PolicyAddonsActivity,
                                            "Failed to delete Policy Addon: $errorMessage",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@PolicyAddonsActivity,
                                "Error deleting Policy Addon: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}