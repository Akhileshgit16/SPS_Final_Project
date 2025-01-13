package com.singlepointsol.navigatioindrawerr.Agent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.singlepointsol.navigatioindrawerr.MainActivity
import com.singlepointsol.navigatioindrawerr.databinding.ActivityAgentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgentBinding
    private val agentService = AgentInstance.getInstance().create(agentApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@AgentActivity, MainActivity::class.java)
                startActivity(backIntent)
            }
        })

        // set listeners to the buttons
        binding.agentGetButton.setOnClickListener {
            val agentID = binding.dropdownAgentId.text.toString() // Get the agent ID from user input
            if (agentID.isNotEmpty()) {
                fetchAgent(agentID) // Fetch agent by ID
            } else {
                Toast.makeText(this, "Please enter a valid Agent ID.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.agentSaveButton.setOnClickListener { saveAgent() }
        binding.agentUpdadteButton.setOnClickListener { updateAgent() }
        binding.agentDeleteButton.setOnClickListener { deleteAgent() }

        // Initialize dropdown for Agent ID
        populateAgentIds()
    }

    private fun populateAgentIds() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = agentService.fetchAgent() // Fetch all agents
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val agentList = response.body()
                        if (!agentList.isNullOrEmpty()) {
                            val agentIds = agentList.map { it.agentID } // Extract Agent IDs
                            val adapter = ArrayAdapter(
                                this@AgentActivity,
                                android.R.layout.simple_dropdown_item_1line,
                                agentIds
                            )
                            binding.dropdownAgentId.setAdapter(adapter) // Set the adapter to the dropdown
                        } else {
                            Toast.makeText(this@AgentActivity, "No agent IDs found!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AgentActivity, "Failed to fetch agent IDs!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AgentActivity", "Error fetching agent IDs: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AgentActivity, "Error fetching agent IDs!", Toast.LENGTH_SHORT).show()
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
            AgentItem(
                agentID = agentId,
                agentName = agentName,
                agentPhone = agentPhone,
                agentEmail = agentEmail,
                licenseCode = licenseCode
            )
        } else null
    }

    private fun fetchAgent(agentID: String) {
        if (agentID.isEmpty()) {
            Toast.makeText(this, "Please provide a valid Agent ID", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = agentService.fetchAgentById(agentID) // Fetch agent by ID
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val agent = response.body()
                        if (agent != null) {
                            binding.dropdownAgentId.setText(agent.agentID)
                            binding.etAgentName.setText(agent.agentName)
                            binding.etAgentPhone.setText(agent.agentPhone)
                            binding.etAgentEmail.setText(agent.agentEmail)
                            binding.etLicenseCode.setText(agent.licenseCode)

                            Toast.makeText(this@AgentActivity, "Fetched agent successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@AgentActivity, "No agent data found!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("AgentActivity", "Failed to fetch agent. Error $errorMessage")
                        Toast.makeText(this@AgentActivity, "Failed to fetch agent: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AgentActivity", "Error fetching agent: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AgentActivity, "Error fetching agent: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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

                val response = agentService.addAgent(agentID, newAgent)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AgentActivity, "Agent added successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        Toast.makeText(this@AgentActivity, "Failed to add agent.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AgentActivity", "Error adding agent: ${e.message}", e)
                withContext(Dispatchers.Main) {
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
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AgentActivity, "Agent updated successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        Toast.makeText(this@AgentActivity, "Failed to update agent.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AgentActivity, "Error updating agent: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AgentActivity, "Agent deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        Toast.makeText(this@AgentActivity, "Failed to delete agent.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AgentActivity", "Error deleting agent: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AgentActivity, "Error deleting agent: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
