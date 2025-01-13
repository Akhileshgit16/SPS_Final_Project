package com.singlepointsol.navigatioindrawerr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.ViewTreeObserver
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge experience for Android 11+
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = true
        window.insetsController?.hide(WindowInsetsCompat.Type.systemBars())

        setContentView(R.layout.activity_splash_screen)
        val imageView = findViewById<ImageView>(R.id.imageView)

        // Wait for the layout to load so we can get the correct image size
        imageView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Get the width of the screen and the ImageView
                val screenWidth = resources.displayMetrics.widthPixels
                val imageWidth = imageView.width

                // Calculate the distance to move from left to center
                val fromXDelta = -imageWidth.toFloat() // Start off-screen
                val toXDelta = (screenWidth / 2f) - (imageWidth / 2f) // Move to the center of the screen

                // Create the TranslateAnimation to move the ImageView
                val animation = TranslateAnimation(
                    fromXDelta, // fromXDelta (start from off-screen left)
                    toXDelta,   // toXDelta (move to center position)
                    0f,         // fromYDelta (no movement on Y-axis)
                    0f          // toYDelta (no movement on Y-axis)
                )

                animation.duration = 6000 // 6 seconds animation
                animation.fillAfter = true // Make sure the image stays in the final position
                imageView.startAnimation(animation) // Start the animation on the ImageView
            }
        })

        // Delay to transition to the next activity (LoginActivity)
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close splash screen activity
        }, 8000) // Show splash for 8 seconds
    }
}
