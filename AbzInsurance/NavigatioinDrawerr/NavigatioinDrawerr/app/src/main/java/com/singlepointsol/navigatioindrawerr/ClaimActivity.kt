
package com.singlepointsol.navigatioindrawerr

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.databinding.ActivityClaimBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ClaimActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClaimBinding
    private val claimService = ClaimInstance.getInstance().create(ClaimApiService::class.java)

    private var isFirstFetchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClaimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set listeners for buttons
        binding.claimGetButton.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchClaim()
                isFirstFetchDone = true // Mark as done after the first fetch
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

        // Set up the claim dropdowns
        setupClaimDropdown()

        // Add DatePicker for Claim Date field
        setupDatePicker()
    }

    private fun setupClaimDropdown() {
        val claimNoEditText: AutoCompleteTextView = findViewById(R.id.et_claim_no)
        val policyNoEditText: AutoCompleteTextView = findViewById(R.id.et_policy_no)
        val policyNoArray = arrayOf("show")
        val claimnoArray = arrayOf("show")
        val claimnoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            claimnoArray
        )
        val policyNoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            policyNoArray
        )
        claimNoEditText.setAdapter(claimnoAdapter)
        policyNoEditText.setAdapter(policyNoAdapter)
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.claimDateEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.claimDateEt.compoundDrawables[2]  // Right drawable
                if (event.rawX >= binding.claimDateEt.right - drawable.bounds.width()) {
                    DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                        binding.claimDateEt.setText(formattedDate)
                    }, year, month, day).show()
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.etIncidentDate.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.etIncidentDate.compoundDrawables[2]  // Right drawable
                if (event.rawX >= binding.etIncidentDate.right - drawable.bounds.width()) {
                    DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                        binding.etIncidentDate.setText(formattedDate)
                    }, year, month, day).show()
                    return@setOnTouchListener true
                }
            }
            false
        }



    }

    private fun clearInputFields() {
        binding.etClaimNo.text?.clear()
        binding.claimDateEt.text?.clear()
        binding.etPolicyNo.text?.clear()
        binding.etIncidentDate.text?.clear()
        binding.etIncidentLocation.text?.clear()
        binding.etIncidentDescription.text?.clear()
        binding.etClaimAmount.text?.clear()
    }

    private fun getClaimFromInput(): ClaimItem? {
        val claimno = binding.etClaimNo.text.toString()
        val claimdate = binding.claimDateEt.text.toString()
        val policyno = binding.etPolicyNo.text.toString()
        val incidatedate = binding.etIncidentDate.text.toString()
        val incidentlocation = binding.etIncidentLocation.text.toString()
        val incidentDescsription = binding.etIncidentDescription.text.toString()
        val claimAmount = binding.etClaimAmount.text.toString()

        return if (claimno.isNotEmpty() && claimdate.isNotEmpty() &&
            policyno.isNotEmpty() && incidatedate.isNotEmpty() && incidentlocation.isNotEmpty() &&
            incidentDescsription.isNotEmpty() && claimAmount.isNotEmpty()
        ) {
            ClaimItem(
                claimNo = claimno,
                claimDate = claimdate,
                policyNo = policyno,
                incidentDate = incidatedate,
                incidentLocaion = incidentlocation,
                incidentLDescription = incidentDescsription,
                claimAmount = claimAmount
            )
        } else null
    }

    private fun fetchClaim() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = claimService.fetchClaimNumbers()
                if (response.isSuccessful) {
                    val claim = response.body()
                    if (!claim.isNullOrEmpty()) {
                        val firstClaim = claim.first()
                        binding.etClaimNo.setText(firstClaim.claimNo)
                        binding.claimDateEt.setText(firstClaim.claimDate)
                        binding.etPolicyNo.setText(firstClaim.policyNo)
                        binding.etIncidentDate.setText(firstClaim.incidentDate)
                        binding.etIncidentLocation.setText(firstClaim.incidentLocaion)
                        binding.etIncidentDescription.setText(firstClaim.incidentLDescription)
                        binding.etClaimAmount.setText(firstClaim.claimAmount)

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
        if (newClaim == null || newClaim.claimNo.isNullOrBlank()) {
            Toast.makeText(this, "Please fill in all fields, including claim No", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val claimno = newClaim.claimNo!!

                // Log the data before making the API call
                Log.d("ClaimActivity", "Attempting to add Claim with No: $claimno, Data: $newClaim")

                val response = claimService.addClaimDetails(claimno, newClaim)

                if (response.isSuccessful) {
                    Log.d("ClaimActivity", "Claim added successfully: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(this@ClaimActivity, "Claim added successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    // Log the response code and error details
                    Log.e("ClaimActivity", "Failed to add Claim: Response Code: ${response.code()}, Error: ${response.errorBody()?.string()}")
                    runOnUiThread {
                        Toast.makeText(this@ClaimActivity, "Failed to add Claim: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ClaimActivity", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@ClaimActivity, "Error adding Claim: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateClaim() {
        val updatedClaim = getClaimFromInput()
        val claimno = binding.etClaimNo.text.toString()
        if (updatedClaim == null || claimno.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = claimService.updateClaimDetails(claimno, updatedClaim)
                if (response.isSuccessful) {
                    Log.d("ClaimActivity", "Claim updated: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(this@ClaimActivity, "Claim updated successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("ClaimActivity", "Failed to update claim: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@ClaimActivity, "Failed to update Claim.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("ClaimActivity", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@ClaimActivity, "Error updating Claim.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun deleteClaim() {
        val claimNo = binding.etClaimNo.text.toString()
        if (claimNo.isEmpty()) {
            Toast.makeText(this, "Please enter a Claim No to delete", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = claimService.deleteClaimDetails(claimNo)
                if (response.isSuccessful) {
                    Log.d("ClaimActivity", "Claim deleted.")
                    runOnUiThread {
                        Toast.makeText(this@ClaimActivity, "Claim deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("ClaimActivity", "Failed to delete claim: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@ClaimActivity, "Failed to delete Claim.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("ClaimActivity", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@ClaimActivity, "Error deleting Claim.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val backIntent = Intent(this, MainActivity::class.java)
        startActivity(backIntent)
    }
}