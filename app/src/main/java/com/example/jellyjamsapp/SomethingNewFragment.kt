package com.example.jellyjamsapp

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jellyjamsapp.spotify.SpotifyApi
import com.example.jellyjamsapp.spotify.SpotifyMoodMapper
import com.example.jellyjamsapp.spotify.SpotifyTokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SomethingNewFragment : Fragment(R.layout.fragment_something_new) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var colorOverlay: View
    private lateinit var bubbleContainer: FrameLayout

    private var popSound: MediaPlayer? = null
    private var score = 0

    private val moods = listOf(
        "Happy", "Calm", "Neutral",
        "Anxiety", "Angry", "Depressed"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultText = view.findViewById<TextView>(R.id.randomMoodResult)
        val spinButton = view.findViewById<Button>(R.id.spinButton)

        recyclerView = view.findViewById(R.id.recommendationsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        colorOverlay = view.findViewById(R.id.colorOverlay)
        bubbleContainer = view.findViewById(R.id.bubbleContainer)

        popSound = MediaPlayer.create(requireContext(), R.raw.pop)

        startBubbleSpawner()

        val pulse = ObjectAnimator.ofFloat(colorOverlay, "alpha", 0.75f, 0.9f)
        pulse.duration = 2000
        pulse.repeatMode = ValueAnimator.REVERSE
        pulse.repeatCount = ValueAnimator.INFINITE
        pulse.start()

        spinButton.setOnClickListener {

            it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            spinButton.isEnabled = false

            lifecycleScope.launch {

                var delayTime = 50L

                repeat(15) {
                    val mood = moods.random()
                    resultText.text = mood
                    delay(delayTime)
                    delayTime += 15
                }

                val finalMood = moods.random()
                resultText.text = finalMood

                val colors = getMoodGradient(finalMood)

                if (view is ViewGroup) {
                    TransitionManager.beginDelayedTransition(view)
                }

                applyAnimatedGradient(colorOverlay, colors)

                fetchSpotifyRecommendations(finalMood)

                spinButton.isEnabled = true
            }
        }
    }

    // -----------------------------Spotify

    private fun fetchSpotifyRecommendations(mood: String) {

        val genre = SpotifyMoodMapper.genreForMood(mood)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SpotifyApi::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = SpotifyTokenManager.getValidToken() ?: return@launch

                val response = api.searchTracks("Bearer $token", genre)

                val songs = response.tracks.items.take(10).map {
                    Song(
                        title = it.name,
                        artist = it.artists[0].name,
                        imageUrl = it.album.images[0].url,
                        spotifyUrl = it.external_urls.spotify
                    )
                }

                recyclerView.adapter = SongAdapter(songs)

            } catch (e: Exception) {
                Log.e("SPOTIFY_DEBUG", "Error", e)
            }
        }
    }


    private fun startBubbleSpawner() {
        lifecycleScope.launch {
            while (true) {
                spawnBubble()
                delay((500..1200).random().toLong())
            }
        }
    }

    private fun spawnBubble() {

        val bubble = View(requireContext())

        val size = (80..180).random()
        val params = FrameLayout.LayoutParams(size, size)

        params.leftMargin = (0..bubbleContainer.width.coerceAtLeast(1)).random()
        params.topMargin = bubbleContainer.height

        bubble.layoutParams = params
        bubble.background = createBubbleDrawable()

        bubbleContainer.addView(bubble)

        val drift = (-100..100).random()

        bubble.animate()
            .translationY(-1500f)
            .translationX(drift.toFloat())
            .alpha(0f)
            .setDuration((3000..6000).random().toLong())
            .withEndAction {
                bubbleContainer.removeView(bubble)
            }
            .start()

        bubble.setOnClickListener {
            bubble.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    bubbleContainer.removeView(bubble)
                }
        }
    }

    private fun createBubbleDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#55FFFFFF"))
            setStroke(2, Color.WHITE)
        }
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
        val anim = ValueAnimator.ofArgb(colors[0], colors[1])
        anim.duration = 600

        anim.addUpdateListener {
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(it.animatedValue as Int, colors[1])
            )
            gradient.alpha = 200
            view.background = gradient
        }

        anim.start()
    }



    // keep ALL your bubble + gradient code the same
}