package com.singlepointsol.navigatioindrawerr.ProductAddon

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
import com.singlepointsol.navigatioindrawerr.databinding.ActivityProductAddonBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductAddonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductAddonBinding
    private val productAddOnService =
        ProductAddonInstance.getProductInstance().create(ProductAddonApiService::class.java)

    private var isFirstFetchDone = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductAddonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@ProductAddonActivity, MainActivity::class.java)
                startActivity(backIntent)
            }
        })
        //set listeners to the buttons
        binding.productAddonGetButton.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchProductAddon()
                isFirstFetchDone = true // Mark as done after the first fetch
            } else {
                clearInputFields()
            }
        }
        binding.productAddonPostButton.setOnClickListener {
            saveProductAddon()
        }
        binding.productAddonUpdateButton.setOnClickListener {
            updateProductAddon()
        }
        binding.productAddonDeleteButton.setOnClickListener {
            deleteProductAddon()
        }
        populateDropdowns()
    }


    private fun setupAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        data: List<String?>
    ) {
        val adapter = ArrayAdapter(
            this@ProductAddonActivity,
            android.R.layout.simple_dropdown_item_1line,
            data
        )
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun populateDropdowns() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch Product addOn
                val response =
                    productAddOnService.fetchProductaddOnDetails()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val productAddonList =
                            response.body()

                        if (!productAddonList.isNullOrEmpty()) {
                            val productId = productAddonList.map { it.productID }.distinct()
                            val addonId = productAddonList.map { it.addonID }.distinct()

                            // Setup AutoCompleteTextView Adapters
                            setupAutoCompleteTextView(binding.productIdDropdown, productId)
                            setupAutoCompleteTextView(binding.addonIdDropdown, addonId)
                        } else {
                            // Show message if the list is empty
                            Toast.makeText(
                                this@ProductAddonActivity,
                                "No Product Addon data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(
                            this@ProductAddonActivity,
                            "Failed to fetch Product Addon data! Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "ProductAddonActivity",
                            "Error fetching Product Addon: ${response.code()} - ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions
                Log.e("ProductAddonActivity", "Error fetching Product Addon data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProductAddonActivity,
                        "Error fetching Product Addon data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clearInputFields() {
        binding.productIdDropdown.text?.clear()
        binding.addonIdDropdown.text?.clear()
        binding.productAddonTitleEditText.text?.clear()
        binding.productAddonDescEditText.text?.clear()
    }

    private fun getProductAddonFromInput(): ProductAddonItem? {
        val productId = binding.productIdDropdown.text.toString().trim()
        val addonId = binding.addonIdDropdown.text.toString().trim()
        val addonTitle = binding.productAddonTitleEditText.toString().trim()
        val addonDescription = binding.productAddonDescEditText.text.toString().trim()

        // Ensure all required fields are populated
        return if (productId.isNotEmpty() && addonId.isNotEmpty() && addonTitle.isNotEmpty() &&
            addonDescription.isNotEmpty()
        ) {
            ProductAddonItem(
                productID = productId,
                addonID = addonId,
                addonTitle = addonTitle,
                addonDescription = addonDescription
            )
        } else {
            null  // If any required field is empty, return null
        }
    }


    private fun fetchProductAddon() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = productAddOnService.fetchProductaddOnDetails()
                if (response.isSuccessful) {
                    val productAddon = response.body()
                    if (!productAddon.isNullOrEmpty()) {
                        val firstPolicy = productAddon.first()
                        binding.productIdDropdown.setText(firstPolicy.productID)
                        binding.addonIdDropdown.setText(firstPolicy.addonID)
                        binding.productAddonTitleEditText.setText(firstPolicy.addonTitle)
                        binding.productAddonDescEditText.setText(firstPolicy.addonDescription)
                        Toast.makeText(
                            this@ProductAddonActivity,
                            "Fetched Product Addon successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ProductAddonActivity,
                            "No Product Addon data found!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProductAddonActivity,
                        "Error fetching Product Addon!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("ProductAddonActivity", "Fetch error: ${e.message}")
            }
        }
    }

    private fun saveProductAddon() {
        // Get the data from the input fields
        val newProductAddon = getProductAddonFromInput()

        // Step 2: Check if the newProductAddon is null
        if (newProductAddon == null) {
            // If the data is invalid, show an error message and return early
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Step 3: Safely handle the potential null value of productID and the API call
        val productID = newProductAddon.productID
        if (productID.isEmpty()) {
            Toast.makeText(this, "Please provide a valid Product ID.", Toast.LENGTH_SHORT).show()
            return
        }

        // Step 4: Make the API call using a Coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Making the API call and passing productID and newProductAddon as parameters
                val response = productAddOnService.addProductAddonDetails(
                    productID,  // This will be the path parameter
                    newProductAddon // This is the body that contains addon details
                )

                // Step 5: Handle the response on the Main thread
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Success message when the API call succeeds
                        Toast.makeText(this@ProductAddonActivity, "Product Addon saved successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        // Failure message when the API call fails
                        Toast.makeText(this@ProductAddonActivity, "Failed to save Product Addon.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: NullPointerException) {
                // Catch NullPointerException specifically
                Log.e("ProductAddonActivity", "NullPointerException occurred: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductAddonActivity, "Null pointer exception occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Catch other exceptions like network errors
                Log.e("ProductAddonActivity", "Error saving Product Addon: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductAddonActivity, "Error saving Product Addon.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun updateProductAddon() {
        val updatedProductId = getProductAddonFromInput()
        val productId = binding.productIdDropdown.text.toString()
        if (updatedProductId == null || productId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    productAddOnService.updateProductAddonDetails(productId, updatedProductId)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ProductAddonActivity,
                            "Product Addon updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearInputFields()
                    }
                } else {
                    Log.e(
                        "ProductAddonActivity",
                        "Failed to update Product Addon: ${response.errorBody()?.string()}"
                    )
                    runOnUiThread {
                        Toast.makeText(
                            this@ProductAddonActivity,
                            "Failed to update Product Addon.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductAddonActivity", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@ProductAddonActivity,
                        "Error updating Product Addon.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deleteProductAddon() {
        val productId = binding.productIdDropdown.text.toString().trim()
        val addonId = binding.addonIdDropdown.text.toString().trim()

        if (productId.isEmpty() || addonId.isEmpty()) {
            Toast.makeText(this, "Please enter both policy number and addon ID to delete", Toast.LENGTH_SHORT).show()
            return
        }

        // Confirmation dialog before sending the delete request
        AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete the policy addon with ProductId: $productId and AddonID: $addonId?")
            .setPositiveButton("Delete") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Make the DELETE request
                        val response = productAddOnService.deleteProductAddonDetails(productId, addonId)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@ProductAddonActivity,
                                    "Product Addon deleted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearInputFields()
                            } else {
                                when (response.code()) {
                                    400 -> {
                                        Toast.makeText(
                                            this@ProductAddonActivity,
                                            "Failed to delete Product Addon: The provided addonID does not match the given policy number.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    404 -> {
                                        Toast.makeText(
                                            this@ProductAddonActivity,
                                            "Failed to delete Product Addon: Either the policy number or addon ID does not exist.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    else -> {
                                        val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                                        Toast.makeText(
                                            this@ProductAddonActivity,
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
                                this@ProductAddonActivity,
                                "Error deleting Product Addon: ${e.message}",
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







