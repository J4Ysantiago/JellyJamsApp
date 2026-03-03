package com.example.jellyjamsapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardFragment : Fragment(R.layout.fragment_leaderboard) {

    private lateinit var recyclerView: RecyclerView
    private val adapter = LeaderboardAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        FirebaseFirestore.getInstance()
            .collection("users")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Leaderboard", "Listen failed", error)
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                val users = snapshot.documents.mapNotNull { doc ->
                    val username = doc.getString("username") ?: return@mapNotNull null

                    val score = when (val raw = doc.get("score")) {
                        is Number -> raw.toLong()
                        is String -> raw.toLongOrNull() ?: 0L
                        else -> 0L
                    }

                    LeaderboardUser(
                        userId = doc.id,
                        username = username,
                        score = score
                    )
                }

                adapter.submitList(users)
            }
    }






}