package com.example.jellyjamsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        recyclerView = findViewById(R.id.leaderboardRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load data from Firestore
        loadLeaderboardData()

        // Handle Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_leaderboard
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); true }
                R.id.nav_mood -> { startActivity(Intent(this, MoodActivity::class.java)); true }
                R.id.nav_leaderboard -> true
                R.id.nav_friends -> { startActivity(Intent(this, FriendsActivity::class.java)); true }
                R.id.nav_new -> { startActivity(Intent(this, SomethingNewActivity::class.java)); true }
                else -> false
            }
        }
    }

    private fun loadLeaderboardData() {
        db.collection("users")
            .orderBy("score", Query.Direction.DESCENDING) // Requires an Index in Firebase
            .limit(50)
            .get()
            .addOnSuccessListener { result ->
                val playerList = mutableListOf<Player>()
                for (document in result) {
                    val name = document.getString("username") ?: "Anonymous Jelly"
                    val score = document.getLong("score")?.toInt() ?: 0
                    playerList.add(Player(name, score))
                }

                if (playerList.isEmpty()) {
                    Toast.makeText(this, "No players yet. Be the first!", Toast.LENGTH_SHORT).show()
                }

                recyclerView.adapter = LeaderboardAdapter(playerList)
            }
            .addOnFailureListener { exception ->
                // Check Logcat for the auto-index link if this fails!
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}