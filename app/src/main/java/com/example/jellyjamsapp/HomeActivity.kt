package com.example.jellyjamsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNav = findViewById(R.id.bottomNav)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }






        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> replaceFragment(HomeFragment())

                R.id.nav_mood -> replaceFragment(MoodFragment())

                R.id.nav_leaderboard -> replaceFragment(LeaderboardFragment())

                R.id.nav_friends -> replaceFragment(FriendsFragment())

                R.id.nav_new -> replaceFragment(SomethingNewFragment())

                R.id.nav_settings -> replaceFragment(SettingsFragment())
            }
            true
        }



    }




    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}