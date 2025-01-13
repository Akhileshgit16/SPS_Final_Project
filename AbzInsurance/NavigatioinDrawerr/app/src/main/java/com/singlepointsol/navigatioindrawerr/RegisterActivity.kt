package com.singlepointsol.navigatioindrawerr
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEdittext: EditText
    private lateinit var registerEmailEdittext: EditText
    private lateinit var phoneEdittext: EditText
    private lateinit var registerPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        nameEdittext = findViewById(R.id.name_et)
        registerEmailEdittext = findViewById(R.id.registeremail_editText)
        phoneEdittext = findViewById(R.id.phone_et)
        registerPassword = findViewById(R.id.password_editText)
        confirmPassword = findViewById(R.id.confirmpassword_editText)
        registerButton = findViewById(R.id.register)

        registerButton.setOnClickListener {
            val name = nameEdittext.text.toString()
            val email = registerEmailEdittext.text.toString()
            val phone = phoneEdittext.text.toString()
            val password = registerPassword.text.toString()
            val confirmPassword = confirmPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show()
                            val loginIntent = Intent(this, LoginActivity::class.java)
                            startActivity(loginIntent)
                            finish()
                        } else {
                            Toast.makeText(this, "Registration failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
