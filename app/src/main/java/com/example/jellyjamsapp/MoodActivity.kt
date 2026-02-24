package com.example.jellyjamsapp

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

private val GridLayout.children: List<View>
    get() = (0 until childCount).map { getChildAt(it) }

class MoodActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var moodTitle: TextView
    private lateinit var moodGrid: GridLayout

    // 1. UPDATED: Added musicUrl to the attributes
    data class MoodAttributes(val colorResId: Int, val musicUrl: String)

    // 2. UPDATED: Map now includes music links (Spotify/YouTube/etc)
    private val moodMap = mapOf(
        "Happy" to MoodAttributes(R.color.happy_color, "https://open.spotify.com/playlist/37i9dQZF1DXdPec7aLTqdg"),
        "Calm" to MoodAttributes(R.color.calm_color, "https://open.spotify.com/playlist/37i9dQZF1DX4PP3R6uYGoT"),
        "Neutral" to MoodAttributes(R.color.neutral_color, "https://open.spotify.com/playlist/37i9dQZF1DX8Ueb9C7V3p0"),
        "Anxiety" to MoodAttributes(R.color.anxiety_color, "https://www.youtube.com/watch?v=lFcSrYw-ARY"),
        "Angry" to MoodAttributes(R.color.angry_color, "https://open.spotify.com/playlist/37i9dQZF1DX3YSRoPdtv9o"),
        "Depressed" to MoodAttributes(R.color.depressed_color, "https://open.spotify.com/playlist/37i9dQZF1DWSqBru69p9vC")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        rootLayout = findViewById(R.id.rootLayout)
        moodTitle = findViewById(R.id.moodTitle)
        moodGrid = findViewById(R.id.moodGrid)

        setMoodListeners()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); true }
                R.id.nav_mood -> true
                R.id.nav_leaderboard -> { startActivity(Intent(this, LeaderboardActivity::class.java)); true }
                R.id.nav_friends -> { startActivity(Intent(this, FriendsActivity::class.java)); true }
                R.id.nav_new -> { startActivity(Intent(this, SomethingNewActivity::class.java)); true }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_mood
    }

    private fun setMoodListeners() {
        moodGrid.children.filterIsInstance<Button>().forEach { button ->
            button.setOnClickListener {
                val mood = button.text.toString()
                updateUIForMood(mood)
                saveMoodEntry(mood)
                // 3. NEW: Trigger music suggestion
                offerMusicForMood(mood)
            }
        }
    }

    // 4. NEW: Function to open music link
    private fun offerMusicForMood(mood: String) {
        val attributes = moodMap[mood] ?: return

        // Using a Snackbar so it doesn't interrupt the user immediately
        val snackbar = Snackbar.make(rootLayout, "Suggested $mood Jam found!", Snackbar.LENGTH_LONG)
        snackbar.setAction("LISTEN") {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(attributes.musicUrl))
            startActivity(intent)
        }
        snackbar.show()
    }

    private fun updateUIForMood(mood: String) {
        val attributes = moodMap[mood] ?: return
        val moodColor = ContextCompat.getColor(this, attributes.colorResId)

        rootLayout.setBackgroundColor(moodColor)

        val contrastColor = if (isColorDark(moodColor)) Color.WHITE else Color.BLACK
        moodTitle.setTextColor(contrastColor)

        moodGrid.children.filterIsInstance<Button>().forEach { btn ->
            if (btn.text.toString() == mood) {
                btn.backgroundTintList = ContextCompat.getColorStateList(this, attributes.colorResId)
                btn.setTextColor(contrastColor)
            } else {
                btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.moodButtonDefault)
                btn.setTextColor(Color.BLACK)
            }
        }
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    private fun saveMoodEntry(mood: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) return

        val moodEntry = hashMapOf(
            "userId" to userId,
            "mood" to mood,
            "timestamp" to Date()
        )

        db.collection("mood_entries").add(moodEntry)
    }
}