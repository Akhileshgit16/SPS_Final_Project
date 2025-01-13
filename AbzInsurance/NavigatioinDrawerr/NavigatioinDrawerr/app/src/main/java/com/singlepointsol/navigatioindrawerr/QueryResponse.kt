package com.singlepointsol.navigatioindrawerr

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.singlepointsol.navigatioindrawerr.databinding.ActivityQueryResponseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class QueryResponse : AppCompatActivity() {
    private lateinit var binding: ActivityQueryResponseBinding
    private val queryresponseService =
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
        setUpDropdown()
        setupDatePicker()


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

    private fun setUpDropdown() {
        val QueryIdEditText:AutoCompleteTextView=findViewById(R.id.query_Id)
        val srNoEditText:AutoCompleteTextView=findViewById(R.id.sr_no)
        val agentIdEditText:AutoCompleteTextView=findViewById(R.id.agent_Id)
        //options
        val queryidArray= arrayOf("show")
        val srNoArray= arrayOf("show")
        val agentIdArray= arrayOf("show")
        val queryIdAdapter = ArrayAdapter(this,
                             android.R.layout.simple_dropdown_item_1line,
                             queryidArray)

        val srNoAdapter=ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            srNoArray)
        val agentUdAdapter=ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            agentIdArray)

        QueryIdEditText.setAdapter(queryIdAdapter)
        srNoEditText.setAdapter(srNoAdapter)
        agentIdEditText.setAdapter(agentUdAdapter)
    }


    private fun clearInputFields() {
        binding.queryId.text?.clear()
        binding.srNo.text?.clear()
        binding.agentId.text?.clear()
        binding.responseDesc.text?.clear()
        binding.responseDate.text?.clear()
    }
    private fun getQueryResponseFromInput(): QueryResponseItem? {
        val queryid = binding.queryId.text.toString()
        val srno = binding.srNo.text.toString()
        val agentid = binding.agentId.text.toString()
        val desc = binding.responseDesc.text.toString()
        val responsedate = binding.responseDate.text.toString()

        return if (queryid.isNotEmpty() && srno.isNotEmpty() &&
            agentid.isNotEmpty() && desc.isNotEmpty() && responsedate.isNotEmpty()
        ) {
            QueryResponseItem(queryID = queryid, srNo = srno, agentID = agentid, description = desc, responseDate = responsedate)
        } else null
    }

    private fun fetchQueryResponse() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = queryresponseService.fetchQueryResponseDetails()
                if (response.isSuccessful) {
                    val agent = response.body()
                    if (!agent.isNullOrEmpty()) {
                        val firstProductaddon = agent.first()
                        binding.queryId.setText(firstProductaddon.queryID)
                        binding.srNo.setText(firstProductaddon.srNo)
                        binding.agentId.setText(firstProductaddon.agentID)
                        binding.responseDesc.setText(firstProductaddon.description)
                        binding.responseDate.setText(firstProductaddon.responseDate)

                        Toast.makeText(this@QueryResponse
                            , "Fetched QueryResponse successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@QueryResponse, "No QueryResponse data found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@QueryResponse, "Error fetching QueryResponses!", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Please fill in all fields, including Query ID and Agent ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Log the data for debugging
        Log.d("QueryResponse", "Attempting to add query response with Data: $newQueryResponse")

        // Coroutine for making the POST request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Send the request
                val response = queryresponseService.addQueryResponseDetails(newQueryResponse)

                // Handle response
                if (response.isSuccessful) {
                    Log.d("QueryResponse", "Query response added successfully: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(this@QueryResponse, "QueryResponse added successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    // Log error details
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("QueryResponse", "Failed to add query response. Response Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        Toast.makeText(this@QueryResponse, "Failed to add QueryResponse: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("QueryResponse", "Exception occurred: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@QueryResponse, "Error adding QueryResponse: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun updateQueryResponse() {
        val updatedQuery = getQueryResponseFromInput()  // Get updated query response data
        val queryid = binding.queryId.text.toString()

        // Check if the queryID and other fields are not empty
        if (updatedQuery == null || queryid.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Set the queryID in the updatedQuery object
        updatedQuery.queryID = queryid

        // Log the data for debugging
        Log.d("QueryResponse", "Attempting to update query response with Data: $updatedQuery")

        // Coroutine for making the PUT request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Send the PUT request
                val response = queryresponseService.updateQueryResponseDetails(updatedQuery)

                // Handle the response
                if (response.isSuccessful) {
                    Log.d("QueryResponse", "Query response updated: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(this@QueryResponse, "Query response updated successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    // Log error details
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("QueryResponse", "Failed to update query response. Response Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        Toast.makeText(this@QueryResponse, "Failed to update QueryResponse: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("QueryResponse", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@QueryResponse, "Error updating QueryResponse: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteQueryResponse() {
        // Get the queryId from the input field (e.g., EditText)
        val queryId = binding.queryId.text.toString().trim()

        // Ensure the queryId is not empty or blank
        if (queryId.isEmpty()) {
            Toast.makeText(this, "Please enter a valid queryID to delete", Toast.LENGTH_SHORT).show()
            return
        }

        // Log the queryId to confirm it is being passed correctly
        Log.d("QueryResponse", "Attempting to delete query with ID: $queryId")

        // Coroutine for background task
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the delete API with the queryId as a path parameter
                val response = queryresponseService.deleteQueryResponseDetails(queryId)

                // Handle the response
                if (response.isSuccessful) {
                    // If the delete was successful, log and show a success message
                    Log.d("QueryResponse", "QueryResponse deleted successfully.")
                    runOnUiThread {
                        Toast.makeText(this@QueryResponse, "QueryResponse deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()  // Optionally clear input fields after deletion
                    }
                } else {
                    // Log the error body for further debugging
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("QueryResponse", "Failed to delete QueryResponse. Response Code: ${response.code()}, Error: $errorBody")

                    // Display an error message to the user
                    runOnUiThread {
                        Toast.makeText(this@QueryResponse, "Failed to delete QueryResponse: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions during the API request
                Log.e("QueryResponse", "Error deleting QueryResponse: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@QueryResponse, "Error deleting QueryResponse: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }







    override fun onBackPressed() {
        super.onBackPressed()
        val backintent= Intent(this,MainActivity::class.java)
        startActivity(backintent)
    }


}