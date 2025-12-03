package com.example.jellyjamsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // --- Check if user is already logged in ---
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // Already logged in → go straight to HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // prevent returning to MainActivity
            return
        }

        // Not logged in → continue showing MainActivity
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Edge-to-edge padding
        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Firestore test
        try {
            val db = FirebaseFirestore.getInstance()
            Log.d(TAG, "Firestore initialized successfully: $db")
        } catch (e: Exception) {
            Log.e(TAG, "Firestore initialization failed", e)
        }

        // Button to navigate to LoginActivity
        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}