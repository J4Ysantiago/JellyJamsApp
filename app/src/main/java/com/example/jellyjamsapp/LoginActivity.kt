package com.example.jellyjamsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpText = findViewById<TextView>(R.id.signUpText)
        val googleButton = findViewById<Button>(R.id.googleButton)
        val facebookButton = findViewById<Button>(R.id.facebookButton)
        val appleButton = findViewById<Button>(R.id.appleButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // TODO: Implement your login logic here
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        googleButton.setOnClickListener {
            // TODO: Implement Google sign-in logic
            Toast.makeText(this, "Google sign-in clicked", Toast.LENGTH_SHORT).show()
        }

        facebookButton.setOnClickListener {
            // TODO: Implement Facebook sign-in logic
            Toast.makeText(this, "Facebook sign-in clicked", Toast.LENGTH_SHORT).show()
        }

        appleButton.setOnClickListener {
            // TODO: Implement Apple sign-in logic
            Toast.makeText(this, "Apple sign-in clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
