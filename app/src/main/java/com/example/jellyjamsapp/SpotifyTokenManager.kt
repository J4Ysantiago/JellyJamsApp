package com.example.jellyjamsapp.spotify

import android.util.Base64
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpotifyTokenManager {

    private var cachedToken: String? = null

    suspend fun getValidToken(): String? {

        if (cachedToken != null) {
            return cachedToken
        }

        val credentials =
            "${SpotifyConfig.CLIENT_ID}:${SpotifyConfig.CLIENT_SECRET}"

        val encoded =
            Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SpotifyAuthApi::class.java)

        val response = api.getAccessToken(
            authHeader = "Basic $encoded"
        )

        if (response.isSuccessful) {
            val token = response.body()?.access_token
            cachedToken = token
            return token
        }

        return null
    }
}