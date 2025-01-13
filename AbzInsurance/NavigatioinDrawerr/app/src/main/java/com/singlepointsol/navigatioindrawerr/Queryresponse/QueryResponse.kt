package com.singlepointsol.navigatioindrawerr.Queryresponse

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.databinding.ActivityQueryResponseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class QueryResponse : AppCompatActivity() {
    private lateinit var binding: ActivityQueryResponseBinding
    private val queryResponseService =
        QueryResponseInstance.getInstance().create(QueryResponseApiService::class.java)

    private var isFirstFetchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQueryResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.responseBtnGet.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchQueryResponse()
                isFirstFetchDone = true // Mark as done after the first fetch
            } else {
                clearInputFields()
            }
        }
        binding.responseBtnSave.setOnClickListener {
            saveQueryResponse()
        }
        binding.esponseBtnUpdate.setOnClickListener {
            updateQueryResponse()
        }
        binding.esponseBtnDelete.setOnClickListener {
            deleteQueryResponse()
        }

        setupDatePicker()
        populateDropdownMenus()


    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.responseDate.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.responseDate.compoundDrawables[2]  // Right drawable
                if (event.rawX >= binding.responseDate.right - drawable.bounds.width()) {
                    DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format(
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                        )
                        binding.responseDate.setText(formattedDate)
                    }, year, month, day).show()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun setupAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        data: List<String?>
    ) {
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
                val response =
                    queryResponseService.fetchQueryResponseDetails() // Make sure this returns a list of vehicles

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val customerQueryList =
                            response.body() // Assuming the response body is a List of Vehicle objects

                        if (!customerQueryList.isNullOrEmpty()) {
                            // Extract unique Registration Numbers and Owner IDs
                            val queryId = customerQueryList.map { it.queryID }.distinct()
                            val srNo = customerQueryList.map { it.srNo }.distinct()
                            val agentId = customerQueryList.map { it.agentID }.distinct()

                            // Setup AutoCompleteTextView Adapters
                            setupAutoCompleteTextView(binding.queryId, queryId)
                            setupAutoCompleteTextView(binding.srNo, srNo)
                            setupAutoCompleteTextView(binding.agentId, agentId)
                        } else {
                            // Show message if the list is empty
                            Toast.makeText(
                                this@QueryResponse,
                                "No  Query Response data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(
                            this@QueryResponse,
                            "Failed to fetch  Query Response data! Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "QueryResponse",
                            "Error fetching  Query Response: ${response.code()} - ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions
                Log.e("QueryResponse", "Error fetching Query Response data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@QueryResponse,
                        "Error fetching  Query Response data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun clearInputFields() {
        binding.queryId.text?.clear()
        binding.srNo.text?.clear()
        binding.agentId.text?.clear()
        binding.responseDesc.text?.clear()
        binding.responseDate.text?.clear()
    }

    private fun getQueryResponseFromInput(): QueryResponseItem? {
        val queryId = binding.queryId.text.toString()
        val srNo = binding.srNo.text.toString()
        val agentId = binding.agentId.text.toString()
        val desc = binding.responseDesc.text.toString()
        val responseDate = binding.responseDate.text.toString()

        return if (queryId.isNotEmpty() && srNo.isNotEmpty() &&
            agentId.isNotEmpty() && desc.isNotEmpty() && responseDate.isNotEmpty()
        ) {
            QueryResponseItem(
                queryID = queryId,
                srNo = srNo,
                agentID = agentId,
                description = desc,
                responseDate = responseDate
            )
        } else null
    }

    private fun fetchQueryResponse() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = queryResponseService.fetchQueryResponseDetails()
                if (response.isSuccessful) {
                    val productAddon = response.body()
                    if (!productAddon.isNullOrEmpty()) {
                        val firstProductAddon = productAddon.first()
                        binding.queryId.setText(firstProductAddon.queryID)
                        binding.srNo.setText(firstProductAddon.srNo)
                        binding.agentId.setText(firstProductAddon.agentID)
                        binding.responseDesc.setText(firstProductAddon.description)
                        binding.responseDate.setText(firstProductAddon.responseDate)

                        Toast.makeText(
                            this@QueryResponse,
                            "Fetched QueryResponse successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@QueryResponse,
                            "No QueryResponse data found!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@QueryResponse,
                        "Error fetching QueryResponses!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("QueryResponse", "Fetch error: ${e.message}")
            }
        }
    }

    private fun saveQueryResponse() {
        val newQueryResponse = getQueryResponseFromInput()

        // Check if the queryID and agentID are provided
        if (newQueryResponse == null || newQueryResponse.queryID.isNullOrBlank() || newQueryResponse.agentID.isNullOrBlank()) {
            Toast.makeText(
                this,
                "Please fill in all fields, including Query ID and Agent ID",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Log the data for debugging
        Log.d("QueryResponse", "Attempting to add query response with Data: $newQueryResponse")

        // Coroutine for making the POST request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Send the request
                val response = queryResponseService.addQueryResponseDetails(newQueryResponse)

                // Handle response
                if (response.isSuccessful) {
                    Log.d("QueryResponse", "Query response added successfully: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(
                            this@QueryResponse,
                            "QueryResponse added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearInputFields()
                    }
                } else {
                    // Log error details
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e(
                        "QueryResponse",
                        "Failed to add query response. Response Code: ${response.code()}, Error: $errorBody"
                    )
                    runOnUiThread {
                        Toast.makeText(
                            this@QueryResponse,
                            "Failed to add QueryResponse: $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("QueryResponse", "Exception occurred: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@QueryResponse,
                        "Error adding QueryResponse: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateQueryResponse() {
        val updatedQuery = getQueryResponseFromInput()  // Get updated query response data
        val queryId = binding.queryId.text.toString()

        // Check if the queryID and other fields are not empty
        if (updatedQuery == null || queryId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Make sure required fields are populated before sending the request
        if (updatedQuery.queryID?.isBlank() == true || updatedQuery.srNo?.isBlank() == true || updatedQuery.agentID?.isBlank() == true) {
            Toast.makeText(
                this,
                "Please fill in all required fields including ClaimStatus, SurveyorName, and SurveyorPhone",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Set the queryID in the updatedQuery object
        updatedQuery.queryID = queryId

        // Log the data for debugging
        Log.d("QueryResponse", "Attempting to update query response with Data: $updatedQuery")

        // Coroutine for making the PUT request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log the updated query request body for better visibility
                Log.d("QueryResponse", "Sending request to update query with body: $updatedQuery")

                // Send the PUT request
                val response = queryResponseService.updateQueryResponseDetails(updatedQuery)

                // Handle the response
                if (response.isSuccessful) {
                    Log.d("QueryResponse", "Query response updated: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(
                            this@QueryResponse,
                            "Query response updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearInputFields()
                    }
                } else {
                    // Log error details
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e(
                        "QueryResponse",
                        "Failed to update query response. Response Code: ${response.code()}, Error: $errorBody"
                    )
                    runOnUiThread {
                        Toast.makeText(
                            this@QueryResponse,
                            "Failed to update QueryResponse: $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("QueryResponse", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@QueryResponse,
                        "Error updating QueryResponse: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    }

    private fun deleteQueryResponse() {
        val queryId = binding.queryId.text.toString().trim()
        val srNo = binding.srNo.text.toString().trim()

        if (queryId.isEmpty() || srNo.isEmpty()) {
            Toast.makeText(this, "Please enter both queryId number and srNo to delete", Toast.LENGTH_SHORT).show()
            return
        }

        // Confirmation dialog before sending the delete request
        AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete the queryId with srNo: $queryId and AddonID: $srNo?")
            .setPositiveButton("Delete") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Make the DELETE request
                        val response = queryResponseService .deleteQueryResponseDetails(queryId, srNo)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@QueryResponse,
                                    "Query Response deleted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearInputFields()
                            } else {
                                when (response.code()) {
                                    400 -> {
                                        Toast.makeText(
                                            this@QueryResponse,
                                            "Failed to delete Query ID: The provided srNo does not match the given queryId.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    404 -> {
                                        Toast.makeText(
                                            this@QueryResponse,
                                            "Failed to delete Query Response: Either the queryId and  does not exist.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    else -> {
                                        val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                                        Toast.makeText(
                                            this@QueryResponse,
                                            "Failed to delete Query Response: $errorMessage",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@QueryResponse,
                                "Error deleting Query Response: ${e.message}",
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