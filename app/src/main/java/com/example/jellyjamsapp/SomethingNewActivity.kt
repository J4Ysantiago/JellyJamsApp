package com.example.jellyjamsapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SomethingNewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_something_new)

        val discoveryText = findViewById<TextView>(R.id.newTitle)
        val jams = listOf("Ocean Waves Lo-Fi", "Electric Jellyfish Techno", "Deep Sea Jazz", "Neon Reef Pop")
        discoveryText.text = "Jam of the Day:\n${jams.random()}"

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_new
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); true }
                R.id.nav_mood -> { startActivity(Intent(this, MoodActivity::class.java)); true }
                R.id.nav_leaderboard -> { startActivity(Intent(this, LeaderboardActivity::class.java)); true }
                R.id.nav_friends -> { startActivity(Intent(this, FriendsActivity::class.java)); true }
                R.id.nav_new -> true
                else -> false
            }
        }
    }
}
