package com.example.jellyjamsapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.jellyjamsapp.spotify.SpotifyApi
import com.example.jellyjamsapp.spotify.SpotifyMoodMapper
import com.example.jellyjamsapp.spotify.SpotifyTokenManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {

    private lateinit var moodPieChart: PieChart
    private lateinit var topMoodText: TextView
    private lateinit var recommendedText: TextView
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        view?.let { recommendedText = it.findViewById(R.id.recommendedPlaylist) }
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        moodPieChart = view.findViewById(R.id.moodPieChart)
        topMoodText = view.findViewById(R.id.topMoodText)

        recommendedText = view.findViewById(R.id.recommendedPlaylist)
        loadWeeklyMoodChart()

        return view
    }

    private fun getStartOfWeekMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun loadWeeklyMoodChart() {

        val uid = auth.currentUser?.uid ?: return
        val startOfWeek = Timestamp(Date(getStartOfWeekMillis()))

        db.collection("users")
            .document(uid)
            .collection("moods")
            .whereGreaterThanOrEqualTo("timestamp", startOfWeek)
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {
                    moodPieChart.clear()
                    topMoodText.text = "No moods this week 🌱"
                    return@addOnSuccessListener
                }

                val moodCount = mutableMapOf<String, Int>()

                for (doc in documents) {
                    val mood = doc.getString("mood") ?: continue
                    moodCount[mood] = moodCount.getOrDefault(mood, 0) + 1
                }

                updatePieChart(moodCount)
                updateTopMood(moodCount)
            }
    }

    private fun updatePieChart(moodCount: Map<String, Int>) {

        val entries = moodCount.map { (mood, count) ->
            PieEntry(count.toFloat(), mood)
        }

        val dataSet = PieDataSet(entries, "This Week's Moods")

        dataSet.colors = listOf(
            "#FFD60A".toColorInt(),
            "#00B4D8".toColorInt(),
            Color.GRAY,
            "#7209B7".toColorInt(),
            "#D00000".toColorInt(),
            "#3A0CA3".toColorInt()
        )

        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        val data = PieData(dataSet)

        moodPieChart.data = data
        moodPieChart.description.isEnabled = false
        moodPieChart.setEntryLabelColor(Color.WHITE)
        moodPieChart.centerText = "This Week"
        moodPieChart.animateY(1000)
        moodPieChart.invalidate()
    }

    private fun updateTopMood(moodCount: Map<String, Int>) {
        val topMood = moodCount.maxByOrNull { it.value }?.key ?: return
        topMoodText.text = "$topMood this week"

        fetchSpotifyRecommendations(topMood)
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

                if (token == null) {
                    recommendedText.text = "Spotify auth failed"
                    return@launch
                }

                Log.d("HOME_SPOTIFY", "Token acquired")

                val response = api.searchTracks(
                    "Bearer $token",
                    genre
                )

                Log.d("HOME_SPOTIFY", "Tracks found: ${response.tracks.items.size}")

                if (response.tracks.items.isEmpty()) {
                    recommendedText.text = "No songs found"
                    return@launch
                }

                val songs = response.tracks.items.take(5).joinToString("\n\n") {
                    "${it.name} – ${it.artists[0].name}"
                }

                recommendedText.text = songs

            } catch (e: Exception) {


            }}}
}