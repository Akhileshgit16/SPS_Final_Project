package com.singlepointsol.navigatioindrawerr.Vehicle

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
import com.singlepointsol.navigatioindrawerr.databinding.ActivityVehicleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class VehicleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleBinding
    private val vehicleService =
        VehicleInstance.getVehicleInstance().create(vehicleApiService::class.java)

    private var isFirstFetchDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for this activity
        binding = ActivityVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@VehicleActivity, MainActivity::class.java)
                startActivity(backIntent)
            }
        })


        //set listeners to the buttons
        binding.vehicleGetButton.setOnClickListener {
            if (!isFirstFetchDone) {
                fetchVehicle()
                isFirstFetchDone = true // Mark as done after the first fetch
            } else {
                clearInputFields()
            }
        }
        binding.vehicleSaveButton.setOnClickListener {
            saveVehicle()

        }
        binding.vehicleUpdateButton.setOnClickListener {
            updateVehicle()
        }
        binding.vehicleDeleteButton.setOnClickListener {
            deleteVehicle()
        }
        setupVehicleDropdown()
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
                // Fetch vehicle details from your service
                val response = vehicleService.fetchVehicleDetails() // Make sure this returns a list of vehicles

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val vehicleList = response.body() // Assuming the response body is a List of Vehicle objects

                        if (!vehicleList.isNullOrEmpty()) {
                            // Extract unique Registration Numbers and Owner IDs
                            val regNumbers = vehicleList.map { it.regNo }.distinct()
                            val ownerIds = vehicleList.map { it.ownerId }.distinct()

                            // Setup AutoCompleteTextView Adapters
                            setupAutoCompleteTextView(binding.regNumberDropdown,   regNumbers)
                            setupAutoCompleteTextView(binding.owneridEditText, ownerIds)
                        } else {
                            Toast.makeText(
                                this@VehicleActivity,
                                "No vehicle data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@VehicleActivity,
                            "Failed to fetch vehicle data! Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "VehicleActivity",
                            "Error fetching vehicles: ${response.code()} - ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions
                Log.e("VehicleActivity", "Error fetching vehicle data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@VehicleActivity,
                        "Error fetching vehicle data!",
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

        binding.regDateEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.regDateEditText.compoundDrawables[2]  // Right drawable
                if (event.rawX >= binding.regDateEditText.right - drawable.bounds.width()) {
                    DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                        binding.regDateEditText.setText(formattedDate)
                    }, year, month, day).show()
                    return@setOnTouchListener true
                }
            }
            false
        }


    }

    private fun setupVehicleDropdown() {


        val regAuthorityEditText: AutoCompleteTextView = findViewById(R.id.regAuthority_et)
        val fuelTypeEditText:AutoCompleteTextView=findViewById(R.id.fuelType_dropdown)
        val makeEditText:AutoCompleteTextView=findViewById(R.id.make_editText)
        val  modelEditText:AutoCompleteTextView=findViewById(R.id.model_editText)

        val regNoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.regAuthority)
        )

        val fuelTypeAdapter=ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.fueltype)
        )

        val makeAdapter=ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(
                R.array.carMakes
            )
        )
        val modelAdapter= ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(
                R.array.carModels
            )
        )

        regAuthorityEditText.setAdapter(regNoAdapter)
        fuelTypeEditText.setAdapter(fuelTypeAdapter)
        makeEditText.setAdapter(makeAdapter)
        modelEditText.setAdapter(modelAdapter)
    }



    private fun clearInputFields() {
        binding.regNumberDropdown.text?.clear()
        binding.regAuthorityEt.text?.clear()
        binding.makeEditText.text?.clear()
        binding.modelEditText.text?.clear()
        binding.fuelTypeDropdown.text?.clear()
        binding.variantEditText.text?.clear()
        binding.engineNumberEditText.text?.clear()
        binding.chassiNumberEditText.text?.clear()
        binding.engineCapacityEditText.text?.clear()
        binding.seatingCapacityEditText.text?.clear()
        binding.mfgYearEditText.text?.clear()
        binding.regDateEditText.text?.clear()
        binding.bodyTypeEditText.text?.clear()
        binding.leaseByEditText.text?.clear()
        binding.owneridEditText.text?.clear()
        //binding.ownerEditText.text?.clear()
    }

    private fun getVehicleFromInput(): VehicleItem? {
        val regNumber = binding.regNumberDropdown.text.toString()
        val regAuthority = binding.regAuthorityEt.text.toString()
        val make = binding.makeEditText.text.toString()
        val model = binding.modelEditText.text.toString()
        val fuelType = binding.fuelTypeDropdown.text.toString()
        val variant = binding.variantEditText.text.toString()
        val engineNumber = binding.engineNumberEditText.text.toString()
        val chassisNumber = binding.chassiNumberEditText.text.toString()
        val engineCapacity = binding.engineCapacityEditText.text.toString()
        val seatingCapacity = binding.seatingCapacityEditText.text.toString()
        val mfgYear = binding.mfgYearEditText.text.toString()
        val regDate = binding.regDateEditText.text.toString()
        val bodyType = binding.bodyTypeEditText.text.toString()
        val leasedBy = binding.leaseByEditText.text.toString()
        val ownerId = binding.owneridEditText.text.toString()
     //   val owner = binding.ownerEditText.text.toString()

        if (listOf(
                regNumber, regAuthority, make, model, fuelType,variant, engineNumber, chassisNumber,
                engineCapacity, seatingCapacity, mfgYear, regDate, bodyType, leasedBy,
                ownerId
            ).any { it.isEmpty() }
        ) {
            return null
        }

        return VehicleItem(
            regNo = regNumber,
            regAuthority = regAuthority,
            make = make,
            model = model,
            fuelType = fuelType,
            variant = variant,
            engineNo = engineNumber,
            chassisNo = chassisNumber,
            engineCapacity = engineCapacity,
            seatingCapacity = seatingCapacity,
            mfgYear = mfgYear,
            regDate = regDate,
            bodyType = bodyType,
            leasedBy = leasedBy,
            ownerId = ownerId,
           // owner = owner
        )
    }

    private fun fetchVehicle() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = vehicleService.fetchVehicleDetails()
                if (response.isSuccessful) {
                    val vehicle = response.body()
                    if (!vehicle.isNullOrEmpty()) {
                        val firstVehicle = vehicle.first()
                        binding.regNumberDropdown.setText(firstVehicle.regNo)
                        binding.regAuthorityEt.setText(firstVehicle.regAuthority)
                        binding.makeEditText.setText(firstVehicle.make)
                        binding.modelEditText.setText(firstVehicle.model)
                        binding.fuelTypeDropdown.setText(firstVehicle.fuelType)
                        binding.variantEditText.setText(firstVehicle.variant)
                        binding.engineNumberEditText.setText(firstVehicle.engineNo)
                        binding.chassiNumberEditText.setText(firstVehicle.chassisNo)
                        binding.engineCapacityEditText.setText(firstVehicle.engineCapacity)
                        binding.seatingCapacityEditText.setText(firstVehicle.seatingCapacity)
                        binding.mfgYearEditText.setText(firstVehicle.mfgYear)
                        binding.regDateEditText.setText(firstVehicle.regDate)
                        binding.bodyTypeEditText.setText(firstVehicle.bodyType)
                        binding.leaseByEditText.setText(firstVehicle.leasedBy)
                        binding.owneridEditText.setText(firstVehicle.ownerId)

                      //  binding.ownerEditText.setText(firstVehicle.owner)

                        Toast.makeText(this@VehicleActivity
                            , "Fetched vehicle successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@VehicleActivity, "No vehicle data found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@VehicleActivity, "Error fetching vehicles!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("VehicleActivity", "Fetch error: ${e.message}")
            }
        }
    }

    private fun saveVehicle() {
        val newVehicle = getVehicleFromInput()
        if (newVehicle == null || newVehicle.regNo.isNullOrBlank()) {
            Toast.makeText(this, "Please fill in all fields, including Vehicle regNO", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val regNO = newVehicle.regNo!!

                // Log the data before making the API call
                Log.d("VehicleActivity", "Attempting to add vehicle with regNo: $regNO, Data: $newVehicle")

                // Check if owner is required and ensure it's populated
                if (newVehicle.ownerId?.isBlank() == true || newVehicle.owner?.isBlank() == true) {
                    runOnUiThread {
                        Toast.makeText(this@VehicleActivity, "Owner information is missing", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Assuming vehicleService.addVehicleDetails expects a VehicleItem as input
                val response = vehicleService.addVehicleDetails(regNO, newVehicle)

                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@VehicleActivity, "Vehicle added successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    // Log the response code and error details
                    Log.e("VehicleActivity", "Failed to add vehicle: Response Code: ${response.code()}, Error: ${response.errorBody()?.string()}")
                    runOnUiThread {
                        Toast.makeText(this@VehicleActivity, "Failed to add vehicle: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("VehicleActivity", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@VehicleActivity, "Error adding vehicle: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateVehicle() {
        val updatedVehicle = getVehicleFromInput()
        val regNO = binding.regNumberDropdown.text.toString()
        if (updatedVehicle == null || regNO.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = vehicleService.updateVehicleDetails(regNO, updatedVehicle)
                if (response.isSuccessful) {
                    Log.d("VehicleActivity", "Vehicle updated: ${response.body()}")
                    runOnUiThread {
                        Toast.makeText(this@VehicleActivity, "Vehicle updated successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("VehicleActivity", "Failed to update Vehicle: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@VehicleActivity, "Failed to update Vehicle.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("VehicleActivity", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@VehicleActivity, "Error updating Vehicle.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun deleteVehicle() {
        val regNO = binding.regNumberDropdown.text.toString()
        if (regNO.isEmpty()) {
            Toast.makeText(this, "Please enter an Vehicle RegNo to delete", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = vehicleService.deleteVehicleDetails(regNO)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@VehicleActivity, "Vehicle deleted successfully!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                } else {
                    Log.e("VehicleActivity", "Failed to delete vehicle: ${response.errorBody()?.string()}")
                    runOnUiThread { Toast.makeText(this@VehicleActivity, "Failed to delete vehicle.", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                Log.e("VehicleActivity", "Error: ${e.message}")
                runOnUiThread { Toast.makeText(this@VehicleActivity, "Error deleting vehicle.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

}


