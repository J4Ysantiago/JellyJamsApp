package com.example.jellyjamsapp.spotify

data class SpotifyAuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)