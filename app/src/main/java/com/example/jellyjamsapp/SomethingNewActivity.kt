package com.example.jellyjamsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class SomethingNewActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_something_new)

        auth = FirebaseAuth.getInstance()


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); true }
                R.id.nav_mood -> { startActivity(Intent(this, MoodActivity::class.java)); true }
                R.id.nav_leaderboard -> { startActivity(Intent(this, LeaderboardActivity::class.java)); true }
                R.id.nav_friends -> { startActivity(Intent(this, FriendsActivity::class.java)); true }
                R.id.nav_new -> true // Already on Something New page
                else -> false
            }
        }

        bottomNav.selectedItemId = R.id.nav_new
    }
}
