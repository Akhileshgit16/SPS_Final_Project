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

    lateinit var name_edittext: EditText
    lateinit var registeremail_edittext: EditText
    lateinit var phone_edittext: EditText
    lateinit var registerpassword: EditText
    lateinit var confirmpassword: EditText
    lateinit var registerbutton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        name_edittext = findViewById(R.id.name_et)
        registeremail_edittext = findViewById(R.id.registeremail_editText)
        phone_edittext = findViewById(R.id.phone_et)
        registerpassword = findViewById(R.id.password_editText)
        confirmpassword = findViewById(R.id.confirmpassword_editText)
        registerbutton = findViewById(R.id.register)

        registerbutton.setOnClickListener {
            val name = name_edittext.text.toString()
            val email = registeremail_edittext.text.toString()
            val phone = phone_edittext.text.toString()
            val password = registerpassword.text.toString()
            val confirmPassword = confirmpassword.text.toString()

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
