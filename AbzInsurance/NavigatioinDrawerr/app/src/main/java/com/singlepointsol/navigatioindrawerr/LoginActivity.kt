package com.singlepointsol.navigatioindrawerr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {

     private lateinit var carsImageView: ImageView
     private lateinit var vehiclesTextView: TextView
     private lateinit var emailEditText: EditText
     private lateinit var passwordEditText: EditText
     private lateinit var forgotPasswordTextButton: Button
     private lateinit var loginButton: Button
     private lateinit var registerTextButton: Button
     private lateinit var accountTextView: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase if not done globally
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        carsImageView = findViewById(R.id.cars_imageView)
        vehiclesTextView = findViewById(R.id.vehicles_textView)
        emailEditText = findViewById(R.id.email_editText)
        passwordEditText = findViewById(R.id.password_editText)
        forgotPasswordTextButton = findViewById(R.id.forgot_textButton)
        loginButton = findViewById(R.id.login_button)
        registerTextButton = findViewById(R.id.register_textButton)
        accountTextView = findViewById(R.id.account_textView)
        auth = FirebaseAuth.getInstance()
        forgotPasswordTextButton.setOnClickListener(this)
        loginButton.setOnClickListener(this)
        registerTextButton.setOnClickListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.login_button -> {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Login successful
                                showCustomToast("Login successful!")
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                // Login failed
                                val errorMessage = task.exception?.message ?: "Login failed"
                                showCustomToast(errorMessage)
                            }
                        }
                } else {
                    showCustomToast("Please enter email and password")
                }
            }

            R.id.register_textButton -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            R.id.forgot_textButton -> {
                val intent = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showCustomToast(message: String) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
