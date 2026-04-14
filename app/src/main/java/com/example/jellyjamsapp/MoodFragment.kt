package com.example.jellyjamsapp

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MoodFragment : Fragment() {

    private lateinit var colorOverlay: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mood, container, false)

        // Overlay
        colorOverlay = view.findViewById(R.id.colorOverlay)

        // 🔥 Pulse animation
        val pulse = ObjectAnimator.ofFloat(
            colorOverlay,
            "alpha",
            0.75f,
            0.9f
        )
        pulse.duration = 2000
        pulse.repeatMode = ValueAnimator.REVERSE
        pulse.repeatCount = ValueAnimator.INFINITE
        pulse.start()

        // Buttons
        view.findViewById<Button>(R.id.btnHappy).setOnClickListener { onMoodSelected("Happy") }
        view.findViewById<Button>(R.id.btnCalm).setOnClickListener { onMoodSelected("Calm") }
        view.findViewById<Button>(R.id.btnNeutral).setOnClickListener { onMoodSelected("Neutral") }
        view.findViewById<Button>(R.id.btnAnxiety).setOnClickListener { onMoodSelected("Anxiety") }
        view.findViewById<Button>(R.id.btnAngry).setOnClickListener { onMoodSelected("Angry") }
        view.findViewById<Button>(R.id.btnDepressed).setOnClickListener { onMoodSelected("Depressed") }

        return view
    }

    private fun onMoodSelected(mood: String) {

        // ✅ Track depressed mood
        if (mood == "Depressed") {
            trackDepressedMood()
        }

        val colors = getMoodGradient(mood)

        TransitionManager.beginDelayedTransition(colorOverlay.parent as ViewGroup)
        applyAnimatedGradient(colorOverlay, colors)

        // Firebase logic
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (uid == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = db.collection("users").document(uid)

        userRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                userRef.set(
                    mapOf(
                        "username" to "Anonymous Jelly",
                        "score" to 0
                    )
                )
            }

            userRef.collection("moods").add(
                mapOf(
                    "mood" to mood,
                    "timestamp" to Timestamp.now()
                )
            )

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

    // ✅ Track depressed count (weekly reset)
    private fun trackDepressedMood() {
        val prefs = requireContext().getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)

        val now = System.currentTimeMillis()
        val lastReset = prefs.getLong("last_reset", 0)

        // Reset every 7 days
        if (now - lastReset > 604800000) {
            prefs.edit()
                .putInt("depressed_count", 0)
                .putLong("last_reset", now)
                .apply()
        }

        val count = prefs.getInt("depressed_count", 0) + 1
        prefs.edit().putInt("depressed_count", count).apply()

        if (count >= 5) {
            showMentalHealthSupport()
        }
    }

    // ✅ Support popup
    private fun showMentalHealthSupport() {
        AlertDialog.Builder(requireContext())
            .setTitle("You're not alone ❤️")
            .setMessage(
                "It looks like you've been feeling down a lot lately.\n\n" +
                        "Talking to someone can really help. Would you like to reach out?"
            )
            .setPositiveButton("Call Support") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:988")
                startActivity(intent)
            }
            .setNegativeButton("Maybe later", null)
            .show()
    }

    private fun getMoodGradient(mood: String): IntArray {
        return when (mood) {
            "Happy" -> intArrayOf(Color.parseColor("#FFE259"), Color.parseColor("#FFA751"))
            "Calm" -> intArrayOf(Color.parseColor("#89F7FE"), Color.parseColor("#66A6FF"))
            "Neutral" -> intArrayOf(Color.parseColor("#E0E0E0"), Color.parseColor("#BDBDBD"))
            "Anxiety" -> intArrayOf(Color.parseColor("#C471F5"), Color.parseColor("#FA71CD"))
            "Angry" -> intArrayOf(Color.parseColor("#F85032"), Color.parseColor("#E73827"))
            "Depressed" -> intArrayOf(Color.parseColor("#434343"), Color.parseColor("#000000"))
            else -> intArrayOf(Color.WHITE, Color.LTGRAY)
        }
    }

    private fun applyAnimatedGradient(view: View, colors: IntArray) {

        val startColor = try {
            (view.background as GradientDrawable).colors?.get(0) ?: colors[0]
        } catch (e: Exception) {
            colors[0]
        }

        val colorAnim = ValueAnimator.ofArgb(startColor, colors[0])
        colorAnim.duration = 600

        colorAnim.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int

            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(animatedColor, colors[1])
            )
            gradient.alpha = 200
            view.background = gradient
        }

        colorAnim.start()
    }
}