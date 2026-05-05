package com.example.jellyjamsapp

data class FriendUser(
    val userId: String,
    val username: String,
    val score: Long,
    val currentSong: String = "",
    val currentMood: String = ""
)