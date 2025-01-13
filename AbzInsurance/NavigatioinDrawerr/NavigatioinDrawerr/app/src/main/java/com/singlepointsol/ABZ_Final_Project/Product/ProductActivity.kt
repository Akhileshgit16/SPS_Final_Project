package com.singlepointsol.ABZ_Final_Project.Product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.singlepointsol.ABZ_Final_Project.MainActivity
import com.singlepointsol.navigatioindrawerr.R
import com.singlepointsol.navigatioindrawerr.databinding.ActivityProductBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private val productService = ProductInstance.getProductInstance().create(productApiService::class.java)

    private var isFirstFetchDone = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Optionally, you can enable the back button on the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        //set listeners to  the  buttons
        binding.productGetButton.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchProduct()
                isFirstFetchDone = true // Mark as done after the first fetch
            } else {
                clearInputFields()
            }
        }
        binding.productSaveButton.setOnClickListener {
            saveProduct()
        }
        binding.productUpdateButton.setOnClickListener {
            updateProduct()

        }
        binding.productDeleteButton.setOnClickListener {
            deleteProduct()
        }

        setupProductIdDropdown()
    }



    private fun clearInputFields() {
        binding.DropdownProductId.text?.clear()
        binding.productNameEditText.text?.clear()
        binding.productDescEditText.text?.clear()
        binding.productUINEditText.text?.clear()
        binding.insuredIntrestsEditText.text?.clear()
        binding.policyCoverageEditText.text?.clear()
    }

    private fun getProductFromInput(): ProductItem? {
        val productId = binding.DropdownProductId.text.toString().trim()
        val productName = binding.productNameEditText.text.toString().trim()
        val productDescription = binding.productDescEditText.text.toString().trim()
        val productUIN = binding.productUINEditText.text.toString().trim()

        // Collect insured interests from the input field (it should be a single string)
        val insuredInterests = binding.insuredIntrestsEditText.text.toString().trim()

        val policyCoverage = binding.policyCoverageEditText.text.toString().trim()

        // Ensure all required fields are populated
        return if (productId.isNotEmpty() && productName.isNotEmpty() &&
            productDescription.isNotEmpty() && productUIN.isNotEmpty() &&
            insuredInterests.isNotEmpty() && policyCoverage.isNotEmpty()
        ) {
            ProductItem(
                productID = productId,
                productName = productName,
                productDescription = productDescription,
                productUIN = productUIN,
                insuredInterests = insuredInterests,  // Ensure this is passed correctly as a string
                policyCoverage = policyCoverage
            )
        } else {
            null  // If any required field is empty, return null
        }
    }










    private fun setupProductIdDropdown() {
        // Initialize the AutoCompleteTextView for Product ID selection
        val productIdEditText: AutoCompleteTextView = findViewById(R.id.Dropdown_ProductId)
        // Array for product ID options
        val productIdArray = arrayOf("show")
        // Create and set the adapter for the AutoCompleteTextView
        val productIdOptionsAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            productIdArray
        )
        productIdEditText.setAdapter(productIdOptionsAdapter)

    }

    private fun fetchProduct() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = productService.fetchProductDetails()
                if (response.isSuccessful) {
                    val products = response.body()
                    if (!products.isNullOrEmpty()) {
                        val firstProduct = products.first()
                        binding.DropdownProductId.setText(firstProduct.productID)
                        binding.productNameEditText.setText(firstProduct.productName)
                        binding.productDescEditText.setText(firstProduct.productDescription)
                        binding.productUINEditText.setText(firstProduct.productUIN)
                        val insuredInterests = binding.insuredIntrestsEditText.text.toString().split(",").map { it.trim() }
                        binding.policyCoverageEditText.setText(firstProduct.policyCoverage)


                        Toast.makeText(this@ProductActivity, "Fetched product successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProductActivity, "No product data found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProductActivity, "Error fetching product!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Fetch error: ${e.message}")
            }
        }
    }

    private fun saveProduct() {
        val newProduct = getProductFromInput()  // Get the product from input fields

        // Check if the product is valid before making the API request
        if (newProduct == null || newProduct.productID.isBlank()) {
            Toast.makeText(this, "Please fill in all fields, including Product ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Log the entire product object to see exactly what is being sent
        Log.d("ProductActivity", "Sending product to server: $newProduct")

        // Send the request to the server asynchronously using a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log the payload for debugging
                Log.d("ProductActivity", "Attempting to add product with ID: ${newProduct.productID}, Payload: $newProduct")

                // Make the POST request to add the product
                val response = productService.addProductDetails(newProduct.productID, newProduct)

                if (response.isSuccessful) {
                    // If the response is successful, handle the successful result
                    Log.d("ProductActivity", "Product added successfully: ${response.body()}")
                    runOnUiThread {
                        // Show success message
                        Toast.makeText(this@ProductActivity, "Product added successfully!", Toast.LENGTH_SHORT).show()
                        // Clear input fields after successful operation
                        clearInputFields()
                    }
                } else {
                    // If the response is not successful, log and show the error
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("ProductActivity", "Failed to add product - Response Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        // Display error message from response
                        Toast.makeText(this@ProductActivity, "Failed to add product: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle any exception during the API request
                Log.e("ProductActivity", "Exception occurred: ${e.message}", e)
                runOnUiThread {
                    // Display error message
                    Toast.makeText(this@ProductActivity, "Error adding product: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    private fun updateProduct() {
        val updateProduct = getProductFromInput()
        val productId = binding.DropdownProductId.text.toString().trim()

        if (updateProduct == null || productId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log the request payload for debugging
                Log.d("ProductActivity", "Attempting to update product with ID: $productId, Payload: $updateProduct")

                // Make the PUT request to update the product
                val response = productService.updateProductDetails(productId, updateProduct)

                // Check if the response is successful (status 200 or 204)
                if (response.isSuccessful) {
                    if (response.code() == 204) {
                        // Handle the case when the server returns 204 No Content
                        Log.d("ProductActivity", "Product updated successfully (no content returned).")
                        runOnUiThread {
                            Toast.makeText(this@ProductActivity, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                            clearInputFields()  // Optionally clear the input fields after success
                        }
                    } else {
                        // Handle the successful case if the server responds with content (status 200)
                        Log.d("ProductActivity", "Product updated successfully: ${response.body()}")
                        runOnUiThread {
                            Toast.makeText(this@ProductActivity, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                            clearInputFields()  // Optionally clear the input fields after success
                        }
                    }
                } else {
                    // Handle error response from the server
                    val errorMessage = response.errorBody()?.string() ?: "No error body"
                    Log.e("ProductActivity", "Failed to update product: $errorMessage")
                    runOnUiThread {
                        Toast.makeText(this@ProductActivity, "Failed to update product: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductActivity", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@ProductActivity, "Error updating product: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }





    private fun deleteProduct() {
        val productId = binding.DropdownProductId.text.toString().trim()

        // Validate the input for product ID
        if (productId.isEmpty()) {
            Toast.makeText(this, "Please enter a Product ID to delete", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the DELETE request to the server
                val response = productService.deleteProductDetails(productId)

                // Check if the response is successful
                if (response.isSuccessful) {
                    Log.d("ProductActivity", "Product deleted successfully.")
                    runOnUiThread {
                        Toast.makeText(this@ProductActivity, "Product deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()  // Optionally clear input fields after successful deletion
                    }
                } else {
                    // Log and show the error message if the delete failed
                    val errorMessage = response.errorBody()?.string() ?: "No error body"
                    Log.e("ProductActivity", "Failed to delete product: $errorMessage")
                    runOnUiThread {
                        Toast.makeText(this@ProductActivity, "Failed to delete product: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Catch any exception that occurs during the API request
                Log.e("ProductActivity", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@ProductActivity, "Error deleting product: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
