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


        // Delay to transition to the next activity (LoginActivity)
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close splash screen activity
        }, 2000) // Show splash for 8 seconds
    }
}
