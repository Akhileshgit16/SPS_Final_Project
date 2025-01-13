package com.singlepointsol.navigatioindrawerr

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup Toolbar
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup Navigation Drawer
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set default fragment on launch
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        // Setup Bottom Navigation
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            handleNavigation(item) // Delegate to shared navigation handler
        }

        // Setup Spinners for Navigation Drawer Items
        setupSpinnerForMenuItem(navigationView, R.id.nav_policy, arrayOf(" ", "Policy", "Policy Addons"))
        setupSpinnerForMenuItem(navigationView, R.id.nav_product, arrayOf(" ", "Product", "Product Addons"))
    }

    private fun setupSpinnerForMenuItem(navigationView: NavigationView, menuItemId: Int, options: Array<String>) {
        val spinnerItem = navigationView.menu.findItem(menuItemId)

        // Inflate the spinner layout
        val spinnerLayout: View = layoutInflater.inflate(R.layout.navigation_spinner_item, null)
        val spinner: Spinner = spinnerLayout.findViewById(R.id.nav_spinner_view)

        // Set up the spinner adapter
        val spinnerAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, options
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // Set the spinner item selection listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                when (selectedItem) {
                    "Policy" -> startActivity(Intent(this@MainActivity, PolicyActivity::class.java))
                    "Policy Addons" -> startActivity(Intent(this@MainActivity, PolicyAddonsActivity::class.java))
                    "Product" -> startActivity(Intent(this@MainActivity, ProductActivity::class.java))
                    "Product Addons" -> startActivity(Intent(this@MainActivity, ProductAddonActivity::class.java))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerItem.actionView = spinnerLayout
    }

    private fun handleNavigation(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Replace fragment for Home
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment()).commit()
            }
            R.id.vehicle_bottom -> {
                // Navigate to VehicleActivity
                startActivity(Intent(this, VehicleActivity::class.java))
            }
            R.id.claim_bottom -> {
                // Navigate to ClaimActivity
                startActivity(Intent(this, ClaimActivity::class.java))
            }
            R.id.nav_claim -> startActivity(Intent(this, ClaimActivity::class.java))
            R.id.nav_agent -> startActivity(Intent(this, AgentActivity::class.java))
            R.id.nav_policy -> startActivity(Intent(this, PolicyActivity::class.java))
            R.id.nav_proposal -> startActivity(Intent(this, ProposalActivity::class.java))
            R.id.nav_customer -> startActivity(Intent(this, CustomerActivity::class.java))
            R.id.nav_customerquery -> startActivity(Intent(this, CustomerQuery::class.java))
            R.id.nav_queryresponse -> startActivity(Intent(this, QueryResponse::class.java))
            R.id.nav_vehicle -> startActivity(Intent(this, VehicleActivity::class.java))
            else -> return false
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return handleNavigation(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
