package com.example.jellyjamsapp

import android.graphics.Color
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val GridLayout.children: List<View>
    get() = (0 until childCount).map { getChildAt(it) }

class MoodActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var moodTitle: TextView

    // private lateinit var moodBackground: ImageView // REMOVED
    private lateinit var moodGrid: GridLayout

    // Simplified Data class: only needs the color ID
    data class MoodAttributes(val colorResId: Int)

    // Map defining mood names and their attributes (no drawable needed)
    private val moodMap = mapOf(
        "Happy" to MoodAttributes(R.color.happy_color),
        "Calm" to MoodAttributes(R.color.calm_color),
        "Neutral" to MoodAttributes(R.color.neutral_color),
        "Anxiety" to MoodAttributes(R.color.anxiety_color),
        "Angry" to MoodAttributes(R.color.angry_color),
        "Depressed" to MoodAttributes(R.color.depressed_color)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize Views by ID
        rootLayout = findViewById(R.id.rootLayout)
        moodTitle = findViewById(R.id.moodTitle)
        // moodBackground = findViewById(R.id.moodBackground) // REMOVED
        moodGrid = findViewById(R.id.moodGrid)

        setMoodListeners()

        // Set up Bottom Navigation (unchanged)

    }

    // Function to set up click listeners (unchanged)
    private fun setMoodListeners() {
        moodGrid.children.filterIsInstance<Button>().forEach { button ->
            button.setOnClickListener {
                val mood = button.text.toString()
                updateUIForMood(mood)
                saveMoodEntry(mood)
            }
        }
    }

    // Function to handle UI updates: color and text
    private fun updateUIForMood(mood: String) {
        val attributes = moodMap[mood] ?: return

        // 1. Get the resolved color
        val moodColor = ContextCompat.getColor(this, attributes.colorResId)

        // 2. Change the main background color (This is the only dynamic background change now)
        rootLayout.setBackgroundColor(moodColor)

        // 3. Change the title text color based on the background darkness
        val contrastColor = if (isColorDark(moodColor)) Color.WHITE else Color.BLACK
        moodTitle.setTextColor(contrastColor)

        // 4. Highlight the selected button and reset others
        moodGrid.children.filterIsInstance<Button>().forEach { btn ->
            if (btn.text.toString() == mood) {
                // Highlight selected button with the mood color
                btn.backgroundTintList =
                    ContextCompat.getColorStateList(this, attributes.colorResId)
                btn.setTextColor(contrastColor)
            } else {
                // Reset other buttons to default style
                btn.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.moodButtonDefault)
                btn.setTextColor(Color.BLACK)
            }
        }
    }

    // Helper to determine text color contrast (unchanged)
    private fun isColorDark(color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    // Function to save the mood entry to Firebase Firestore (unchanged)
    private fun saveMoodEntry(mood: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
            return
        }

        val moodEntry = hashMapOf(
            "mood" to mood,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        db.collection("users")
            .document(uid)
            .collection("moods")
            .add(moodEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "$mood mood recorded successfully!", Toast.LENGTH_SHORT).show()
                Log.d("Firebase", "Mood saved under users/$uid/moods")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save mood: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("Firebase", "Error saving mood", e)
            }
    }
}