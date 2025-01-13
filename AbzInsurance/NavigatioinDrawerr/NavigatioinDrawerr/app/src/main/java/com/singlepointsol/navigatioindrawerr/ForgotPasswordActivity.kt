package com.singlepointsol.navigatioindrawerr

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var emailEditText: EditText
    lateinit var resetPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)


        emailEditText = findViewById(R.id.email_et)
        resetPasswordButton = findViewById(R.id.reset_password_button)

        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isNotEmpty()) {

                Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_SHORT)
                    .show()

                finish()
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}