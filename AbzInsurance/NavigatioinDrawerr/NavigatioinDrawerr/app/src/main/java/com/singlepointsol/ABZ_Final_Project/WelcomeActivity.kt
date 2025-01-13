package com.singlepointsol.ABZ_Final_Project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.singlepointsol.ABZ_Final_Project.LoginActivity
import com.singlepointsol.navigatioindrawerr.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        // Initialize the ViewPager2
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // Define the list of slides
        val slideList = listOf(R.layout.slide_welcome, R.layout.slide_buyinsurance, R.layout.slide_manage_policies)

        // Set the adapter for the ViewPager
        val adapter = SlideAdapter(slideList)
        viewPager.adapter = adapter

        // Add listener for ViewPager scrolling
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val skipButton: Button = findViewById(R.id.skip_button)

                if (position == slideList.size - 1) {
                    // Show the "Skip" button on the last slide
                    skipButton.visibility = View.VISIBLE
                } else {
                    // Hide the "Skip" button if not on the last slide
                    skipButton.visibility = View.GONE
                }
                // Display "Swipe Next" toast for the first two slides
                if (position == 0 || position == 1) {
                    Toast.makeText(this@WelcomeActivity, "Swipe Next Please", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Handle the "Skip" button click on the last slide
        val skipButton: Button = findViewById(R.id.skip_button)
        skipButton.setOnClickListener {
            // Navigate to MainActivity when clicked
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Close the WelcomeActivity to avoid navigating back
        }
    }
}
