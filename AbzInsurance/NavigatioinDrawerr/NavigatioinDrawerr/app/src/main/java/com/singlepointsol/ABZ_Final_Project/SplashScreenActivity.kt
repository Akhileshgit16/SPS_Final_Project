package com.singlepointsol.navigatioindrawerr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.ViewTreeObserver
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.singlepointsol.ABZ_Final_Project.WelcomeActivity
import com.singlepointsol.navigatioindrawerr.R

class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge experience for Android 11+
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = true
        window.insetsController?.hide(WindowInsetsCompat.Type.systemBars())

        setContentView(R.layout.activity_splash_screen)
        val imageView = findViewById<ImageView>(R.id.abzsplash)

        // Wait for the layout to load so we can get the correct image size
        imageView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Get the width of the screen and the ImageView
                val screenWidth = resources.displayMetrics.widthPixels
                val imageWidth = imageView.width

                // Calculate the distance to move from right to left
                val fromXDelta = screenWidth.toFloat() // Start off-screen (right)
                val toXDelta = -(imageWidth.toFloat()) // Move to left (completely off-screen)

                // Create the TranslateAnimation to move the ImageView
                val animation = TranslateAnimation(
                    fromXDelta, // fromXDelta (start from off-screen right)
                    toXDelta,   // toXDelta (move to off-screen left)
                    0f,         // fromYDelta (no movement on Y-axis)
                    0f          // toYDelta (no movement on Y-axis)
                )

                animation.duration = 2800 // 2.8 seconds animation
                animation.fillAfter = true // Make sure the image stays in the final position
                imageView.startAnimation(animation) // Start the animation on the ImageView
            }
        })

        // Delay to transition to the next activity (LoginActivity)
        Handler().postDelayed({
            startActivity(Intent(this@SplashScreenActivity, WelcomeActivity::class.java))
            finish() // Close splash screen activity
        }, 3010);  // Show splash for 6 seconds
    }
}
