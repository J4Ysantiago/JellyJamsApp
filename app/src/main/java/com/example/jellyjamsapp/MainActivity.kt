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
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        //hi this is bri!
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase and write a test value
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore

        val testData = hashMapOf(
            "message" to "Hello from Jelly Jams!",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("testCollection")
            .add(testData)
            .addOnSuccessListener { docRef ->
                Log.d("FirebaseTest", "✅ Data added with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseTest", "❌ Error adding document", e)
            }
    }
}
