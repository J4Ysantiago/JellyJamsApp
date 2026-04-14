package com.example.jellyjamsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // RecyclerView setup
        recyclerView = findViewById(R.id.leaderboardRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = LeaderboardAdapter()
        recyclerView.adapter = adapter

        loadLeaderboardData()

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_leaderboard

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_mood -> {
                    startActivity(Intent(this, MoodActivity::class.java))
                    true
                }
                R.id.nav_leaderboard -> true
                else -> false
            }
        }
    }

    private fun loadLeaderboardData() {
        db.collection("users")
            .orderBy("score")
            .get()
            .addOnSuccessListener { documents ->

                val leaderboardList = documents.mapNotNull { doc ->
                    val username = doc.getString("username")
                    val score = doc.getLong("score")
                    val userId = doc.id

                    if (username != null && score != null) {
                        LeaderboardUser(
                            userId = userId,
                            username = username,
                            score = score.toInt()
                        )
                    } else null
                }
                    .sortedByDescending { it.score }

                adapter.submitList(leaderboardList)
            }
    }
}