package com.example.jellyjamsapp.spotify

data class SpotifySearchResponse(
    val tracks: Tracks
)

data class Tracks(
    val items: List<Track>
)

data class Track(
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val external_urls: ExternalUrls
)

data class Artist(
    val name: String
)

data class Album(
    val images: List<Image>
)

data class Image(
    val url: String
)

data class ExternalUrls(
    val spotify: String
)