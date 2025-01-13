package com.singlepointsol.navigatioindrawerr.Customerquery

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.MainActivity
import com.singlepointsol.navigatioindrawerr.R
import com.singlepointsol.navigatioindrawerr.databinding.ActivityCustomerQueryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CustomerQuery : AppCompatActivity() {
    private lateinit var binding:ActivityCustomerQueryBinding
    private var customerQueryService=
        CustomerQueryInstance.getInstance().create(CustomerQueryApiService::class.java)

    private var isFirstFetchDone = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerQueryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@CustomerQuery, MainActivity::class.java)
                startActivity(backIntent)
            }
        })

        //set listeners to to the buttons
        binding.customerqueryBtnGet.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchCustomerQuery()
                isFirstFetchDone = true // Mark as done after the first fetch
            } else {
                clearInputFields()
                   }
        }
        binding.customerqueryBtnSave.setOnClickListener {
            saveCustomerQuery()
        }
        binding.customerqueryBtnUpdate.setOnClickListener {
            updateCustomerQuery()
        }
        binding.customerqueryBtnDelete.setOnClickListener {
            deleteCustomerQuery()
        }
        setupDropDown()
        setupDatePicker()
        populateDropdownMenus()


    }
    private fun setupDropDown() {
        val statusEditText:AutoCompleteTextView=findViewById(R.id.cus_query_status)
        val statusArray= arrayOf("A","R")
        //options
        val statusAdapter=ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            statusArray)
        statusEditText.setAdapter(statusAdapter)

    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.cusQueryDate.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.cusQueryDate.compoundDrawables[2]  // Right drawable
                if (event.rawX >= binding.cusQueryDate.right - drawable.bounds.width()) {
                    DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format(
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                        )
                        binding.cusQueryDate.setText(formattedDate)
                    }, year, month, day).show()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
    private fun setupAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView, data: List<String?>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            data
        )
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun populateDropdownMenus() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch vehicle details from your service
                val response = customerQueryService.fetchCustomerQuery() // Make sure this returns a list of vehicles

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val customerQueryList = response.body() // Assuming the response body is a List of Vehicle objects

                        if (!customerQueryList.isNullOrEmpty()) {
                            // Extract unique Registration Numbers and Owner IDs
                            val queryId = customerQueryList.map { it.queryID }.distinct()
                            val customerId = customerQueryList.map { it.customerID }.distinct()

                            // Setup AutoCompleteTextView Adapters
                            setupAutoCompleteTextView(binding.customerqueryqueryId,   queryId)
                            setupAutoCompleteTextView(binding.customerQuerycustomerId,customerId)
                        } else {
                            // Show message if the list is empty
                            Toast.makeText(
                                this@CustomerQuery,
                                "No customer Query data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(
                            this@CustomerQuery,
                            "Failed to fetch  customer Query data! Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "CustomerQuery",
                            "Error fetching  customer Query: ${response.code()} - ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions
                Log.e("CustomerQuery", "Error fetching  customer Query data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CustomerQuery,
                        "Error fetching  customer Query data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun clearInputFields() {
        binding.customerqueryqueryId.text?.clear()
        binding.customerQuerycustomerId.text?.clear()
        binding.cusQueryDescEt.text?.clear()
        binding.cusQueryDate.text?.clear()
        binding.cusQueryStatus.text?.clear()
    }
    private fun getCustomerQueryFromInput(): CustomerQueryItem? {
        val queryId = binding.customerqueryqueryId.text.toString()
        val customerId = binding.customerQuerycustomerId.text.toString()
        val desc = binding.cusQueryDescEt.text.toString()
        val queryDate = binding.cusQueryDate.text.toString()
        val status = binding.cusQueryStatus.text.toString()

        return if (queryId.isNotEmpty() && customerId.isNotEmpty() &&
            desc.isNotEmpty() && queryDate.isNotEmpty() && status.isNotEmpty()
        ) {
            CustomerQueryItem(queryID = queryId, customerID = customerId, description = desc, queryDate = queryDate, status = status)
        } else null
    }


    private fun fetchCustomerQuery() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = customerQueryService.fetchCustomerQuery()
                if (response.isSuccessful) {
                    val customerQuery = response.body()
                    if (!customerQuery.isNullOrEmpty()) {
                        val firstQuery = customerQuery.first()
                        binding.customerqueryqueryId.setText(firstQuery.queryID)
                        binding.customerQuerycustomerId.setText(firstQuery.customerID)
                        binding.cusQueryDescEt.setText(firstQuery.description)
                        binding.cusQueryDate.setText(firstQuery.queryDate)
                        binding.cusQueryStatus.setText(firstQuery.status)

                        Toast.makeText(this@CustomerQuery, "Fetched CustomerQuery successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CustomerQuery, "No CustomerQuery data found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CustomerQuery, "Error fetching CustomerQuery!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CustomerQuery", "Fetch error: ${e.message}")
            }
        }
    }
    private fun saveCustomerQuery() {
        val newCustomerQuery = getCustomerQueryFromInput()

        // Validate input fields
        if (newCustomerQuery == null || newCustomerQuery.queryID.isBlank()) {
            Toast.makeText(this, "Please fill in all fields, including Query ID", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Send the POST request
                val response = customerQueryService.addCustomerQueryDetails(newCustomerQuery)

                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CustomerQuery, "Query saved successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()  // Clear input fields after success
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("CustomerQuery", "Failed to save query. Response Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        Toast.makeText(this@CustomerQuery, "Failed to save query: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("CustomerQuery", "Exception occurred: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@CustomerQuery, "Error saving query: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateCustomerQuery() {
        val updatedQuery = getCustomerQueryFromInput()
        val queryId = binding.customerqueryqueryId.text.toString()
        if (updatedQuery == null || queryId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = customerQueryService.updateCustomerQueryDetails(queryId, updatedQuery)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CustomerQuery, "CustomerQuery updated successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("CustomerQuery", "Failed to update CustomerQuery: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@CustomerQuery, "Failed to update CustomerQuery.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("CustomerQuery", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@CustomerQuery, "Error updating CustomerQuery.", Toast.LENGTH_SHORT).show() }
            }
        }
    }
    private fun deleteCustomerQuery() {
        val queryId = binding.customerqueryqueryId.text.toString()
        if (queryId.isEmpty()) {
            Toast.makeText(this, "Please enter an queryID to delete", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = customerQueryService.deleteCustomerQueryDetails(queryId)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CustomerQuery, "CustomerQuery deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("CustomerQuery", "Failed to delete CustomerQuery: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@CustomerQuery, "Failed to delete CustomerQuery.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("CustomerQuery", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@CustomerQuery, "Error deleting CustomerQuery.", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}