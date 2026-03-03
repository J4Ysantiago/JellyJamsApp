package com.example.jellyjamsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MoodFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mood, container, false)

        // Buttons
        val btnHappy = view.findViewById<Button>(R.id.btnHappy)
        val btnCalm = view.findViewById<Button>(R.id.btnCalm)
        val btnNeutral = view.findViewById<Button>(R.id.btnNeutral)
        val btnAnxiety = view.findViewById<Button>(R.id.btnAnxiety)
        val btnAngry = view.findViewById<Button>(R.id.btnAngry)
        val btnDepressed = view.findViewById<Button>(R.id.btnDepressed)

        // Click listeners
        btnHappy.setOnClickListener { onMoodSelected("Happy") }
        btnCalm.setOnClickListener { onMoodSelected("Calm") }
        btnNeutral.setOnClickListener { onMoodSelected("Neutral") }
        btnAnxiety.setOnClickListener { onMoodSelected("Anxiety") }
        btnAngry.setOnClickListener { onMoodSelected("Angry") }
        btnDepressed.setOnClickListener { onMoodSelected("Depressed") }


        return view
    }



    private fun onMoodSelected(mood: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (uid == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = db.collection("users").document(uid)

        // 1️⃣ Ensure user document exists
        userRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                userRef.set(
                    mapOf(
                        "username" to "Anonymous Jelly",
                        "score" to 0
                    )
                )
            }

            // 2️⃣ Save mood
            userRef.collection("moods").add(
                mapOf(
                    "mood" to mood,
                    "timestamp" to Timestamp.now()
                )
            )



            // 3️⃣ Increment score
            userRef.update("score", FieldValue.increment(1))
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Saved: $mood (+1 point)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


}
