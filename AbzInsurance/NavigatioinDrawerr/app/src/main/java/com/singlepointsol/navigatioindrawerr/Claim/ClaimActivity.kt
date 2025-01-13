package com.singlepointsol.navigatioindrawerr.Claim

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
import com.google.android.material.textfield.TextInputEditText
import com.singlepointsol.navigatioindrawerr.MainActivity
import com.singlepointsol.navigatioindrawerr.R
import com.singlepointsol.navigatioindrawerr.databinding.ActivityClaimBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ClaimActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClaimBinding
    private val claimService = ClaimInstance.getInstance().create(ClaimApiService::class.java)
    private var isFirstFetchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClaimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@ClaimActivity, MainActivity::class.java)
                startActivity(backIntent)
            }
        })

        // Set listeners for buttons
        binding.claimGetButton.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchClaim()
                isFirstFetchDone = true
            } else {
                clearInputFields()
            }
        }

        binding.claimSaveButton.setOnClickListener {
            saveClaim()
        }

        binding.claimUpdateButton.setOnClickListener {
            updateClaim()
        }

        binding.claimDeleteButton.setOnClickListener {
            deleteClaim()
        }

        // Add DatePicker for Claim Date field,incident Date and survey Date
        setupDatePicker()
        populateDropdownMenus()
        setupClaimStatus()
    }

    private fun setupClaimStatus() {
        val claimStatusEditText: AutoCompleteTextView = binding.etClaimStatus
        val claimAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.claimStatus)
        )
        claimStatusEditText.setAdapter(claimAdapter)
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
                val response = claimService.fetchClaimDetails()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val policyList = response.body()

                        if (!policyList.isNullOrEmpty()) {
                            val claimNo = policyList.mapNotNull { it.claimNo }.distinct()
                            val policyNo = policyList.mapNotNull { it.policyNo }.distinct()

                            setupAutoCompleteTextView(binding.etPolicyNo, policyNo)
                            setupAutoCompleteTextView(binding.etClaimNo, claimNo)
                        } else {
                            Toast.makeText(
                                this@ClaimActivity,
                                "No claim data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ClaimActivity,
                            "Failed to fetch claim data! Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "ClaimActivity",
                            "Error fetching claim: ${response.code()} - ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ClaimActivity", "Error fetching claim data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ClaimActivity,
                        "Error fetching claim data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        setupSingleDatePicker(binding.claimDateEt, year, month, day)
        setupSingleDatePicker(binding.etIncidentDate, year, month, day)
        setupSingleDatePicker(binding.etSurveyDate, year, month, day)
    }

    private fun setupSingleDatePicker(editText: TextInputEditText, year: Int, month: Int, day: Int) {
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = editText.compoundDrawables[2]
                if (drawable != null && event.rawX >= editText.right - drawable.bounds.width()) {
                    DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                        editText.setText(formattedDate)
                    }, year, month, day).show()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun clearInputFields() {
        binding.apply {
            etClaimNo.text?.clear()
            claimDateEt.text?.clear()
            etPolicyNo.text?.clear()
            etIncidentDate.text?.clear()
            etIncidentLocation.text?.clear()
            etIncidentDescription.text?.clear()
            etClaimAmount.text?.clear()
            surveynameEt.text?.clear()
            etSurveyphone.text?.clear()
            etSurveyDate.text?.clear()
            etSurveyDesc.text?.clear()
            etClaimStatus.text?.clear()
        }
    }

    private fun getClaimFromInput(): ClaimItem? {
        binding.apply {
            val claimNo = etClaimNo.text?.toString().orEmpty()
            val claimDate = claimDateEt.text?.toString().orEmpty()
            val policyNo = etPolicyNo.text?.toString().orEmpty()
            val incidentDate = etIncidentDate.text?.toString().orEmpty()
            val incidentLocation = etIncidentLocation.text?.toString().orEmpty()
            val incidentDescription = etIncidentDescription.text?.toString().orEmpty()
            val claimAmount = etClaimAmount.text?.toString().orEmpty()
            val surveyName = surveynameEt.text?.toString().orEmpty()
            val surveyPhone = etSurveyphone.text?.toString().orEmpty()
            val surveyDate = etSurveyDate.text?.toString().orEmpty()
            val surveyDesc = etSurveyDesc.text?.toString().orEmpty()
            val claimStatus = etClaimStatus.text?.toString().orEmpty()

            return if (listOf(
                    claimNo, claimDate, policyNo, incidentDate, incidentLocation,
                    incidentDescription, claimAmount, surveyName, surveyPhone,
                    surveyDate, surveyDesc, claimStatus
                ).all { it.isNotEmpty() }
            ) {
                ClaimItem(
                    claimNo, claimDate, policyNo, incidentDate, incidentLocation,
                    incidentDescription, claimAmount, surveyName, surveyPhone, surveyDate,
                    surveyDesc, claimStatus
                )
            } else null
        }
    }

    private fun fetchClaim() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = claimService.fetchClaimDetails()
                if (response.isSuccessful) {
                    val claims = response.body()
                    if (!claims.isNullOrEmpty()) {
                        val firstClaim = claims.first()
                        binding.apply {
                            etClaimNo.setText(firstClaim.claimNo.orEmpty())
                            claimDateEt.setText(firstClaim.claimDate.orEmpty())
                            etPolicyNo.setText(firstClaim.policyNo.orEmpty())
                            etIncidentDate.setText(firstClaim.incidentDate.orEmpty())
                            etIncidentLocation.setText(firstClaim.incidentLocation.orEmpty())
                            etIncidentDescription.setText(firstClaim.incidentDescription.orEmpty())
                            etClaimAmount.setText(firstClaim.claimAmount.orEmpty())
                            surveynameEt.setText(firstClaim.surveyorName.orEmpty())
                            etSurveyphone.setText(firstClaim.surveyorPhone.orEmpty())
                            etSurveyDate.setText(firstClaim.surveyDate.orEmpty())
                            etSurveyDesc.setText(firstClaim.surveyDescription.orEmpty())
                            etClaimStatus.setText(firstClaim.claimStatus.orEmpty())
                        }
                        Toast.makeText(this@ClaimActivity, "Fetched Claim successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ClaimActivity, "No Claim data found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ClaimActivity, "Error fetching Claim!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ClaimActivity", "Fetch error: ${e.message}")
            }
        }
    }

    private fun saveClaim() {
        val newClaim = getClaimFromInput()
        if (newClaim == null) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = newClaim.claimNo?.let { claimService.addClaimDetails(it, newClaim) }
                withContext(Dispatchers.Main) {
                    if (response != null) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ClaimActivity, "Claim saved successfully!", Toast.LENGTH_SHORT).show()
                            clearInputFields()
                        } else {
                            Toast.makeText(this@ClaimActivity, "Failed to save claim.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ClaimActivity, "Error saving claim.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateClaim() {
        val updatedClaim = getClaimFromInput()
        if (updatedClaim == null) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    updatedClaim.claimNo?.let { claimService.updateClaimDetails(it, updatedClaim) }
                withContext(Dispatchers.Main) {
                    if (response != null) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ClaimActivity, "Claim updated successfully!", Toast.LENGTH_SHORT).show()
                            clearInputFields()
                        } else {
                            Toast.makeText(this@ClaimActivity, "Failed to update claim.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ClaimActivity, "Error updating claim.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteClaim() {
        val claimNo = binding.etClaimNo.text?.toString().orEmpty()
        if (claimNo.isEmpty()) {
            Toast.makeText(this, "Please enter a Claim No to delete.", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = claimService.deleteClaimDetails(claimNo)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ClaimActivity, "Claim deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        Toast.makeText(this@ClaimActivity, "Failed to delete claim.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ClaimActivity, "Error deleting claim.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
