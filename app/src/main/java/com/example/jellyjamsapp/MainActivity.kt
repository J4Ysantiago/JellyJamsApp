package com.example.jellyjamsapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //hi this is bri
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Initialize Firebase and write a test value
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
