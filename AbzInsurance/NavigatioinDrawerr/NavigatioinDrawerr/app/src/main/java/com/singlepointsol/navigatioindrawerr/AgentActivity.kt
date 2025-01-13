package com.singlepointsol.navigatioindrawerr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.databinding.ActivityAgentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AgentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgentBinding
    private val agentService = AgentInstance.getInstance().create(agentApiService::class.java)

    private var isFirstFetchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Button Click Listeners
        binding.agentGetButton.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchAgent()
                isFirstFetchDone = true // Mark as done after the first fetch
            } else {
                clearInputFields()
            }
        }
        binding.agentSaveButton.setOnClickListener { saveAgent() }
        binding.agentUpdadteButton.setOnClickListener { updateAgent() }
        binding.agentDeleteButton.setOnClickListener { deleteAgent() }

        // Initialize dropdown for Agent ID
        setupAgentIdDropdown()
    }

    private fun setupAgentIdDropdown() {
        val agentIdEditText: AutoCompleteTextView = findViewById(R.id.dropdown_agent_id)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the API to fetch agent data
                val response = agentService.fetchAgent()
                if (response.isSuccessful) {
                    val agentList = response.body()
                    if (!agentList.isNullOrEmpty()) {
                        val agentIdArray = agentList.map { it.agentID }.toTypedArray()

                        // Update UI with the dropdown data
                        runOnUiThread {
                            val agentIdAdapter = ArrayAdapter(
                                this@AgentActivity,
                                android.R.layout.simple_dropdown_item_1line,
                                agentIdArray
                            )
                            agentIdEditText.setAdapter(agentIdAdapter)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@AgentActivity,
                                "No Agent IDs found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@AgentActivity,
                            "Error fetching Agent IDs",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AgentActivity", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@AgentActivity,
                        "Error fetching Agent IDs: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun clearInputFields() {
        binding.dropdownAgentId.text?.clear()
        binding.etAgentName.text?.clear()
        binding.etAgentPhone.text?.clear()
        binding.etAgentEmail.text?.clear()
        binding.etLicenseCode.text?.clear()
    }

    private fun getAgentFromInput(): AgentItem? {
        val agentId = binding.dropdownAgentId.text.toString()
        val agentName = binding.etAgentName.text.toString()
        val agentPhone = binding.etAgentPhone.text.toString()
        val agentEmail = binding.etAgentEmail.text.toString()
        val licenseCode = binding.etLicenseCode.text.toString()

        return if (agentId.isNotEmpty() && agentName.isNotEmpty() &&
            agentPhone.isNotEmpty() && agentEmail.isNotEmpty() && licenseCode.isNotEmpty()
        ) {
            AgentItem(agentID = agentId, agentName = agentName, agentPhone = agentPhone, agentEmail = agentEmail, licenseCode = licenseCode)
        } else null
    }

    private fun fetchAgent() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = agentService.fetchAgent()
                if (response.isSuccessful) {
                    val agent = response.body()
                    if (!agent.isNullOrEmpty()) {
                        val firstProductaddon = agent.first()
                        binding.dropdownAgentId.setText(firstProductaddon.agentID)
                        binding.etAgentName.setText(firstProductaddon.agentName)
                        binding.etAgentPhone.setText(firstProductaddon.agentPhone)
                        binding.etAgentEmail.setText(firstProductaddon.agentEmail)
                        binding.etLicenseCode.setText(firstProductaddon.licenseCode)

                        Toast.makeText(this@AgentActivity
                            , "Fetched agent successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AgentActivity, "No agent data found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AgentActivity, "Error fetching agents!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Fetch error: ${e.message}")
            }
        }
    }




    private fun saveAgent() {
        val newAgent = getAgentFromInput()
        if (newAgent == null || newAgent.agentID.isNullOrBlank()) {
            Toast.makeText(this, "Please fill in all fields, including Agent ID", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val agentID = newAgent.agentID!!

                // Log the data before making the API call
                Log.d("AgentActivity", "Attempting to add agent with ID: $agentID, Data: $newAgent")

                val response = agentService.addAgent(agentID, newAgent)

                if (response.isSuccessful) {
                    Log.d("AgentActivity", "Agent added successfully: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(this@AgentActivity, "Agent added successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    // Log the response code and error details
                    Log.e("AgentActivity", "Failed to add agent: Response Code: ${response.code()}, Error: ${response.errorBody()?.string()}")
                    runOnUiThread {
                        Toast.makeText(this@AgentActivity, "Failed to add agent: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AgentActivity", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@AgentActivity, "Error adding agent: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateAgent() {
        val updatedAgent = getAgentFromInput()
        val agentId = binding.dropdownAgentId.text.toString()
        if (updatedAgent == null || agentId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = agentService.updateAgent(agentId, updatedAgent)
                if (response.isSuccessful) {
                    Log.d("AgentActivity", "Agent updated: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(this@AgentActivity, "Agent updated successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("AgentActivity", "Failed to update agent: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@AgentActivity, "Failed to update agent.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("AgentActivity", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@AgentActivity, "Error updating agent.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun deleteAgent() {
        val agentId = binding.dropdownAgentId.text.toString()
        if (agentId.isEmpty()) {
            Toast.makeText(this, "Please enter an Agent ID to delete", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = agentService.deleteAgent(agentId)
                if (response.isSuccessful) {
                    Log.d("AgentActivity", "Agent deleted.")
                    runOnUiThread {
                        Toast.makeText(this@AgentActivity, "Agent deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("AgentActivity", "Failed to delete agent: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@AgentActivity, "Failed to delete agent.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("AgentActivity", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@AgentActivity, "Error deleting agent.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val backintent=Intent(this,MainActivity::class.java)
        startActivity(backintent)
    }
}
