package com.example.jellyjamsapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.jellyjamsapp.spotify.SpotifyApi
import com.example.jellyjamsapp.spotify.SpotifyMoodMapper
import com.example.jellyjamsapp.spotify.SpotifyTokenManager
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SomethingNewFragment : Fragment(R.layout.fragment_something_new) {

    private lateinit var recommendationText: TextView

    private val moods = listOf(
        "Happy",
        "Calm",
        "Neutral",
        "Anxiety",
        "Angry",
        "Depressed"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultText = view.findViewById<TextView>(R.id.randomMoodResult)
        val spinButton = view.findViewById<Button>(R.id.spinButton)
        recommendationText = view.findViewById(R.id.spinRecommendations)

        spinButton.setOnClickListener {
            val randomMood = moods.random()
            resultText.text = randomMood

            changeBackground(view, randomMood)
            fetchSpotifyRecommendations(randomMood)
        }
    }

    private fun changeBackground(view: View, mood: String) {
        val color = when (mood) {
            "Happy" -> Color.parseColor("#FFD60A")
            "Calm" -> Color.parseColor("#00B4D8")
            "Neutral" -> Color.GRAY
            "Anxiety" -> Color.parseColor("#7209B7")
            "Angry" -> Color.parseColor("#D00000")
            "Depressed" -> Color.parseColor("#3A0CA3")
            else -> Color.WHITE
        }

        view.setBackgroundColor(color)
    }

    private fun fetchSpotifyRecommendations(mood: String) {

        val genre = SpotifyMoodMapper.genreForMood(mood)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SpotifyApi::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val token = SpotifyTokenManager.getValidToken()
                Log.d("SPOTIFY_DEBUG", "Token: $token")

                if (token == null) {
                    recommendationText.text = "Token failed"
                    return@launch
                }

                val response = api.searchTracks(
                    "Bearer $token",
                    genre
                )

                Log.d("SPOTIFY_DEBUG", "Response size: ${response.tracks.items.size}")

                val songs = response.tracks.items.take(5).joinToString("\n\n") {
                    "${it.name} – ${it.artists[0].name}"
                }

                recommendationText.text = songs

            } catch (e: Exception) {
                Log.e("SPOTIFY_DEBUG", "Error: ${e.message}", e)
                recommendationText.text = "Spotify failed"
            }
        }



    }
}