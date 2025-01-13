package com.singlepointsol.navigatioindrawerr.Proposal

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.MainActivity
import com.singlepointsol.navigatioindrawerr.databinding.ActivityProposalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class ProposalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProposalBinding

    private var isFirstFetchDone = false

    private val proposalService =
        ProposalInstance.getInstance().create(ProposalApiService::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProposalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@ProposalActivity, MainActivity::class.java)
                startActivity(backIntent)
            }
        })

        binding.proposalGetButton.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchProposal()
                isFirstFetchDone = true
            } else {
                clearInputFields()
            }
        }

        binding.proposalSaveButton.setOnClickListener {
            addProposal()
        }
        binding.proposalUpdateButton.setOnClickListener {
            updateProposal()
        }
        binding.proposalDeleteButton.setOnClickListener {
            deleteProposal()
        }


        setupDatePicker()
        populateDropdownMenus()
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

                val response = proposalService.fetchProposalDetails()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val proposalList = response.body()

                        if (!proposalList.isNullOrEmpty()) {
                            val proposalNo=proposalList.map{it.proposalNo}.distinct()
                            val regNumbers = proposalList.map { it.regNo }.distinct()
                            val ownerIds = proposalList.map { it.customerID }.distinct()
                            val productId = proposalList.map { it.productID }.distinct()
                            val agentIds = proposalList.map { it.agentID }.distinct()

                            // Setup AutoCompleteTextView Adapters
                            setupAutoCompleteTextView(binding.proposalNoEditText,proposalNo)
                            setupAutoCompleteTextView(binding.dropdownRegistrationNo, regNumbers)
                            setupAutoCompleteTextView(binding.dropdownCustomerId, ownerIds)
                            setupAutoCompleteTextView(binding.dropdownProductId,productId)
                            setupAutoCompleteTextView(binding.dropdownAgentId,agentIds)

                        } else {
                            // Show message if the list is empty
                            Toast.makeText(
                                this@ProposalActivity,
                                "No proposal data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(
                            this@ProposalActivity,
                            "Failed to fetch proposal data! Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "ProposalActivity",
                            "Error fetching proposals: ${response.code()} - ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions
                Log.e("ProposalActivity", "Error fetching proposal  data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProposalActivity,
                        "Error fetching proposal data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
        // Open DatePicker when clicking on date icon
        private fun setupDatePicker() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            binding.etFromDate.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawable = binding.etFromDate.compoundDrawables[2]  // Right drawable
                    if (event.rawX >= binding.etFromDate.right - drawable.bounds.width()) {
                        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                            binding.etFromDate.setText(formattedDate)
                        }, year, month, day).show()
                        return@setOnTouchListener true
                    }
                }
                false
            }

            binding.etToDate.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawable = binding.etToDate.compoundDrawables[2]  // Right drawable
                    if (event.rawX >= binding.etFromDate.right - drawable.bounds.width()) {
                        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                            binding.etToDate.setText(formattedDate)
                        }, year, month, day).show()
                        return@setOnTouchListener true
                    }
                }
                false
            }



        }

    private fun clearInputFields() {
        binding.proposalNoEditText.text?.clear()
        binding.dropdownRegistrationNo.text?.clear()
        binding.dropdownProductId.text?.clear()
        binding.dropdownCustomerId.text?.clear()
        binding.etFromDate.text?.clear()
        binding.etToDate.text?.clear()
        binding.etIdv.text?.clear()
        binding.dropdownAgentId.text?.clear()
        binding.basicAmount.text?.clear()
        binding.totalAmount.text?.clear()
    }

    private fun getProposalFromInput(): ProposalItem? {
        val proposalNo = binding.proposalNoEditText.text.toString()
        val regNumber = binding.dropdownRegistrationNo.text.toString()
        val productId = binding.dropdownProductId.text.toString()
        val customerId = binding.dropdownCustomerId.text.toString()
        val fromDate = binding.etFromDate.text.toString()
        val toDate = binding.etToDate.text.toString()
        val idv= binding.etIdv.text.toString()
        val agentId = binding.dropdownAgentId.text.toString()
        val basicAmount = binding.basicAmount.text.toString()
        val totalAmount = binding.totalAmount.text.toString()

        return if (proposalNo.isNotEmpty() && regNumber.isNotEmpty() &&
            productId.isNotEmpty() && customerId.isNotEmpty() && fromDate.isNotEmpty() &&
            toDate.isNotEmpty() && idv.isNotEmpty() && agentId.isNotEmpty() &&
            basicAmount.isNotEmpty() && totalAmount.isNotEmpty()
        ) {
            ProposalItem(
                proposalNo = proposalNo,
                regNo = regNumber,
                productID = productId,
                customerID = customerId,
                fromDate = fromDate,
                toDate = toDate,
                idv = idv,
                agentID = agentId,
                basicAmount = basicAmount,
                totalAmount = totalAmount
            )
        } else null
    }


    private fun fetchProposal() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = proposalService.fetchProposalDetails()
                if (response.isSuccessful) {
                    val proposal = response.body()
                    if (!proposal.isNullOrEmpty()) {
                        val firstProposal = proposal.first()
                        binding.proposalNoEditText.setText(firstProposal.proposalNo)
                        binding.dropdownRegistrationNo.setText(firstProposal.regNo)
                        binding.dropdownProductId.setText(firstProposal.productID)
                        binding.dropdownCustomerId.setText(firstProposal.customerID)
                        binding.etFromDate.setText(firstProposal.fromDate)
                        binding.etToDate.setText(firstProposal.toDate)
                        binding.etIdv.setText(firstProposal.idv)
                        binding.dropdownAgentId.setText(firstProposal.agentID)
                        binding.basicAmount.setText(firstProposal.basicAmount)
                        binding.totalAmount.setText(firstProposal.totalAmount)
                        Toast.makeText(
                            this@ProposalActivity,
                            "Fetched proposal successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ProposalActivity,
                            "No proposal data found!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProposalActivity,
                        "Error fetching proposal!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("ProposalActivity", "Fetch error: ${e.message}")
            }
        }
    }
    private fun addProposal() {
        val newProposal = getProposalFromInput()  // Get the proposal from input fields

        // Check if the proposal is valid before making the API request
        if (newProposal == null || newProposal.proposalNo.isBlank() || newProposal.fromDate.isNullOrBlank()) {
            Toast.makeText(this, "Please fill in all fields, including Proposal No and From Date", Toast.LENGTH_SHORT).show()
            return
        }

        // Make sure the dates are in the correct format
        newProposal.fromDate = formatDate(newProposal.fromDate)
        newProposal.toDate = formatDate(newProposal.toDate)

        // Send the request to the server asynchronously using a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the POST request to add the proposal
                val response = proposalService.addProposalDetails(newProposal.proposalNo, newProposal)

                if (response.isSuccessful) {
                    // If the response is successful, handle the successful result
                    runOnUiThread {
                        // Show success message
                        Toast.makeText(this@ProposalActivity, "Proposal added successfully!", Toast.LENGTH_SHORT).show()
                        // Clear input fields after successful operation
                        clearInputFields()
                    }
                } else {
                    // If the response is not successful, log and show the error
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("ProposalActivity", "Failed to add proposal - Response Code: ${response.code()}, Error: $errorBody")
                    runOnUiThread {
                        // Display error message from response
                        Toast.makeText(this@ProposalActivity, "Failed to add proposal: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle any exception during the API request
                Log.e("ProposalActivity", "Exception occurred: ${e.message}", e)
                runOnUiThread {
                    // Display error message
                    Toast.makeText(this@ProposalActivity, "Error adding proposal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Helper function to ensure correct date format (ISO 8601)
    private fun formatDate(dateString: String?): String {
        return try {
            // Attempt to parse and reformat the date string into ISO 8601 format
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateString ?: "")
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            // In case of invalid date format, return a default or empty value
            Log.e("ProposalActivity", "Invalid date format: $e")
            ""
        }
    }



    private fun updateProposal() {
        val updateProposal = getProposalFromInput()
        val proposalNo = binding.proposalNoEditText.text.toString().trim()

        if (updateProposal == null || proposalNo.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log the request payload for debugging
                Log.d("ProposalActivity", "Attempting to update policy with No: $proposalNo, Payload: $updateProposal")

                // Make the PUT request to update the product
                val response = proposalService.updateProposalDetailstDetails(proposalNo, updateProposal)

                // Check if the response is successful (status 200 or 204)
                if (response.isSuccessful) {
                    if (response.code() == 204) {
                        // Handle the case when the server returns 204 No Content
                        Log.d("ProposalActivity", "policy updated successfully (no content returned).")
                        runOnUiThread {
                            Toast.makeText(this@ProposalActivity, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                            clearInputFields()  // Optionally clear the input fields after success
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ProposalActivity, "policy updated successfully!", Toast.LENGTH_SHORT).show()
                            clearInputFields()  // Optionally clear the input fields after success
                        }
                    }
                } else {
                    // Handle error response from the server
                    val errorMessage = response.errorBody()?.string() ?: "No error body"
                    Log.e("ProposalActivity", "Failed to update policy: $errorMessage")
                    runOnUiThread {
                        Toast.makeText(this@ProposalActivity, "Failed to update product: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ProposalActivity", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@ProposalActivity, "Error updating policy: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun deleteProposal() {
        val proposalNo = binding.proposalNoEditText.text.toString().trim()
        // Validate the input for product ID
        if (proposalNo.isEmpty()) {
            Toast.makeText(this, "Please enter a Proposal No to delete", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the DELETE request to the server
                val response = proposalService.deleteProposalDetails(proposalNo)
                // Check if the response is successful
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@ProposalActivity, "Proposal deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()  // Optionally clear input fields after successful deletion
                    }
                } else {
                    // Log and show the error message if the delete failed
                    val errorMessage = response.errorBody()?.string() ?: "No error body"
                    Log.e("ProposalActivity", "Failed to delete Proposal: $errorMessage")
                    runOnUiThread {
                        Toast.makeText(this@ProposalActivity, "Failed to delete Proposal: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Catch any exception that occurs during the API request
                Log.e("ProposalActivity", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@ProposalActivity, "Error deleting Proposal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
