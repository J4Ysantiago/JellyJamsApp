package com.example.jellyjamsapp.spotify

object SpotifyMoodMapper {

    fun genreForMood(mood: String): String {
        return when (mood.lowercase()) {
            "happy" -> "pop"
            "calm" -> "chill"
            "neutral" -> "indie"
            "anxiety" -> "ambient"
            "angry" -> "rock"
            "depressed" -> "acoustic"
            else -> "pop"
        }
    }
}