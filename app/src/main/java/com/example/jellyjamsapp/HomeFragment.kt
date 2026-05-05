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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jellyjamsapp.spotify.SpotifyApi
import com.example.jellyjamsapp.spotify.SpotifyMoodMapper
import com.example.jellyjamsapp.spotify.SpotifyTokenManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
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
    private lateinit var recyclerView: RecyclerView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        moodPieChart = view.findViewById(R.id.moodPieChart)
        topMoodText = view.findViewById(R.id.topMoodText)

        recyclerView = view.findViewById(R.id.recommendationsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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
                    topMoodText.text = "No moods today 🌱"
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

        val dataSet = PieDataSet(entries, "" )
        dataSet.colors = listOf(
            "#FFD60A".toColorInt(),
            "#00B4D8".toColorInt(),
            Color.GRAY,
            "#7209B7".toColorInt(),
            "#D00000".toColorInt(),
            "#3A0CA3".toColorInt()
        )


        dataSet.valueTextSize = 18f
        dataSet.valueTextColor = Color.BLACK

        val data = PieData(dataSet)


        moodPieChart.data = data



        val legend = moodPieChart.legend

        legend.isWordWrapEnabled = true
        legend.textSize = 14f
        legend.textColor = Color.DKGRAY

        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL

        legend.setDrawInside(false)

        legend.xEntrySpace = 24f
        legend.yEntrySpace = 8f
        legend.formToTextSpace = 10f
        legend.formSize = 12f
        legend.form = Legend.LegendForm.CIRCLE

        legend.yOffset = 10f

        moodPieChart.legend.isEnabled = entries.size > 1
        moodPieChart.description.isEnabled = false
        moodPieChart.setEntryLabelColor(Color.WHITE)
        moodPieChart.centerText = "Moods felt this week"
        moodPieChart.animateY(1000)
        moodPieChart.invalidate()
    }



    private fun updateTopMood(moodCount: Map<String, Int>) {
        val topMood = moodCount.maxByOrNull { it.value }?.key ?: return
        topMoodText.text = "Feeling $topMood today"
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
                    return@launch
                }

                val response = api.searchTracks("Bearer $token", genre)

                val songs = response.tracks.items.take(5).map {
                    Song(
                        title = it.name,
                        artist = it.artists[0].name,
                        imageUrl = it.album.images[0].url,
                        spotifyUrl = it.external_urls.spotify
                    )


                }

                recyclerView.adapter = SongAdapter(songs)

            } catch (e: Exception) {

                Log.e("SPOTIFY", "Error", e)
            }
        }
    }
}