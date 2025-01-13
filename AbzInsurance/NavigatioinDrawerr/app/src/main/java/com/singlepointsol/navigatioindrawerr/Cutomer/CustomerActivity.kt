package com.singlepointsol.navigatioindrawerr.Cutomer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.MainActivity
import com.singlepointsol.navigatioindrawerr.databinding.ActivityCustomerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCustomerBinding
    private val customerService= CustomerInstance.getInstance().create(CustomerApiService::class.java)

    private var isFirstFetchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for the activity
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@CustomerActivity, MainActivity::class.java)
                startActivity(backIntent)
            }
        })

        //set listeners to the buttons
        binding.btnGet.setOnClickListener {

            if (!isFirstFetchDone) {
                fetchCustomer()
                isFirstFetchDone = true // Mark as done after the first fetch
            } else {
                clearInputFields()
            }
        }
        binding.btnSave.setOnClickListener {
            saveCustomer()
        }
        binding.btnUpdate.setOnClickListener {
            updateCustomer()
        }
        binding.btnDelete.setOnClickListener {
            deleteCustomer()
        }


        //dropdown for customerId
        populateCustomerIds()
    }
    private fun populateCustomerIds() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = customerService.fetchCustomerDetails()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val customerList = response.body()
                        if (!customerList.isNullOrEmpty()) {
                            val customerIds = customerList.map { it.customerID } // Extract customer IDs
                            val adapter = ArrayAdapter(
                                this@CustomerActivity,
                                android.R.layout.simple_dropdown_item_1line,
                                customerIds
                            )
                            binding.customerId.setAdapter(adapter) // Set the adapter to the dropdown
                        } else {
                            Toast.makeText(this@CustomerActivity, "No customer IDs found!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@CustomerActivity, "Failed to fetch customer IDs!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("CustomerActivity", "Error fetching customer IDs: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CustomerActivity, "Error fetching customer IDs!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun clearInputFields() {
        binding.customerId.text?.clear()
        binding.customerName.text?.clear()
        binding.customerPhone.text?.clear()
        binding.customerEmail.text?.clear()
        binding.customerAddress.text?.clear()
    }

    private fun getCustomerFromInput(): CustomerItem? {
        val customerId = binding.customerId.text.toString()
        val customerName = binding.customerName.text.toString()
        val customerPhone = binding.customerPhone.text.toString()
        val customerEmail = binding.customerEmail.text.toString()
        val customerAddress = binding.customerAddress.text.toString()

        return if (customerId.isNotEmpty() && customerName.isNotEmpty() &&
            customerPhone.isNotEmpty() && customerEmail.isNotEmpty() && customerAddress.isNotEmpty()
        ) {
            CustomerItem(customerID = customerId, customerName = customerName,customerPhone = customerPhone, customerEmail = customerEmail, customerAddress= customerAddress)
        } else null
    }

    private fun fetchCustomer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = customerService.fetchCustomerDetails()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val customer = response.body()
                        Log.d("CustomerActivity", "Fetched customer data: $customer")
                        if (!customer.isNullOrEmpty()) {
                            val firstCustomer = customer.first()
                            binding.customerId.setText(firstCustomer.customerID)
                            binding.customerName.setText(firstCustomer.customerName)
                            binding.customerPhone.setText(firstCustomer.customerPhone)
                            binding.customerEmail.setText(firstCustomer.customerEmail)
                            binding.customerAddress.setText(firstCustomer.customerAddress)

                            Toast.makeText(this@CustomerActivity, "Fetched customer successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@CustomerActivity, "No customer data found!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("CustomerActivity", "Error fetching customers: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@CustomerActivity, "Error fetching customers!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("CustomerActivity", "Fetch error: ${e.message}", e)
            }
        }
    }

    private fun saveCustomer() {
        val newCustomer = getCustomerFromInput()  // Get customer details from the input fields

        // Check if customerID is not null or blank
        if (newCustomer == null || newCustomer.customerID.isNullOrBlank()) {
            Toast.makeText(this, "Please fill in all fields, including customerID", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val customerID = newCustomer.customerID!!  // Get customer ID from input
                // Make the API call with customerId as part of the URL and newCustomer as the request body
                val response = customerService.addCustomerDetails(customerID, newCustomer)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CustomerActivity, "Customer added successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()  // Clear the input fields after successful submission
                    }
                } else {
                    // Log the response code and error details
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("CustomerActivity", "Failed to add Customer: Response Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        Toast.makeText(this@CustomerActivity, "Failed to add Customer: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Log the exception and show a Toast message
                Log.e("CustomerActivity", "Exception: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@CustomerActivity, "Error adding Customer: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun updateCustomer() {
        val updatedCustomer = getCustomerFromInput()  // Get the updated customer data from input fields
        val customerId = binding.customerId.text.toString()
        // Check if customerId or updated customer data is missing
        if (updatedCustomer == null || customerId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Run the update request on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the API call to update the customer details
                val response = customerService.updateCustomerDetails(customerId, updatedCustomer)

                // Check if the request was successful
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CustomerActivity, "Customer updated successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()  // Clear input fields after successful update
                    }
                } else {
                    // Log the error response and show a failure message
                    Log.e("CustomerActivity", "Failed to update customer: Response Code: ${response.code()}")
                    runOnUiThread {
                        Toast.makeText(this@CustomerActivity, "Failed to update Customer. Please check logs.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Log the exception and show a failure message
                Log.e("CustomerActivity", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@CustomerActivity, "Error updating Customer.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun deleteCustomer() {
        val customerId = binding.customerId.text.toString()
        if (customerId.isEmpty()) {
            Toast.makeText(this, "Please enter an customer ID to delete", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = customerService.deleteCustomerDetails(customerId)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CustomerActivity, "Customer deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("CustomerActivity", "Failed to delete Customer: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@CustomerActivity, "Failed to delete Customer.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("CustomerActivity", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@CustomerActivity, "Error deleting customer.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

}
