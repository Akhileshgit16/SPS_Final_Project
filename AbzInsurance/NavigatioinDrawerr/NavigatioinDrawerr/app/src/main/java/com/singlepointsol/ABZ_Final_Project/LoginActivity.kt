package com.singlepointsol.ABZ_Final_Project

import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
import com.singlepointsol.ABZ_Final_Project.RegisterActivity
import com.singlepointsol.navigatioindrawerr.ForgotPasswordActivity
import com.singlepointsol.navigatioindrawerr.R

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var forgotPasswordTextButton: TextView
    lateinit var signInButton: Button
    lateinit var signupButton: Button
    lateinit var eyeIcon: ImageView  // Reference for eye icon
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase if not done globally
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge() // Enable edge-to-edge for full screen content
        setContentView(R.layout.logintest) // Ensure the correct layout file

        // Find views by ID
        emailEditText = findViewById(R.id.email_et)
        passwordEditText = findViewById(R.id.password_et)
        forgotPasswordTextButton = findViewById(R.id.forgot_txtBtn)
        signInButton = findViewById(R.id.signin_button)
        signupButton = findViewById(R.id.signup_Button)
        eyeIcon = findViewById(R.id.eye_icon)
        auth = FirebaseAuth.getInstance()

        // Set OnClickListeners
        forgotPasswordTextButton.setOnClickListener(this)
        signInButton.setOnClickListener(this)
        signupButton.setOnClickListener(this)

        // Toggle password visibility on eye icon click
        eyeIcon.setOnClickListener {
            if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                // Show password
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeIcon.setImageResource(R.drawable.eye)  // Set open eye icon
            } else {
                // Hide password
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eyeIcon.setImageResource(R.drawable.hidden)  // Set closed eye icon
            }

            // Move the cursor to the end of the text
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Handle system window insets (padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginmain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.signin_button -> {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    // Disable sign-in button during authentication process
                    signInButton.isEnabled = false
                    showCustomToast("Logging in...")

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            signInButton.isEnabled = true // Re-enable button
                            if (task.isSuccessful) {
                                // Login successful
                                showCustomToast("Login successful!")
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Optionally finish the login activity
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

            R.id.signup_Button -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            R.id.forgot_txtBtn -> {
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
