package com.example.jellyjamsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.leaderboardRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LeaderboardAdapter()
        recyclerView.adapter = adapter

        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->

                val users = documents.mapNotNull { doc ->
                    val username = doc.getString("username")
                    val scoreLong = doc.getLong("score") ?: 0L

                    if (username != null) {
                        LeaderboardUser(
                            userId = doc.id,
                            username = username,
                            score = scoreLong.toInt()
                        )
                    } else null
                }.sortedByDescending { it.score }

                adapter.submitList(users)
            }
    }
}