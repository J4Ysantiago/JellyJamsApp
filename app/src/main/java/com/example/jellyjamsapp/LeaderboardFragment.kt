package com.example.jellyjamsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
            .addOnSuccessListener { snapshot ->

                val users = snapshot.documents.mapNotNull { doc ->
                    val username = doc.getString("username") ?: return@mapNotNull null
                    val score = doc.getLong("score") ?: 0

                    LeaderboardUser(
                        userId = doc.id,
                        username = username,
                        score = score
                    )
                }.sortedByDescending { it.score }

                updatePodium(users)
                updateRecyclerView(users.drop(3))
            }
    }



    private fun updatePodium(users: List<LeaderboardUser>) {
        val root = view ?: return

        if (users.isNotEmpty()) {
            root.findViewById<TextView>(R.id.firstName).text = users[0].username
            root.findViewById<TextView>(R.id.firstScore).text = users[0].score.toString()
        }

        if (users.size > 1) {
            root.findViewById<TextView>(R.id.secondName).text = users[1].username
            root.findViewById<TextView>(R.id.secondScore).text = users[1].score.toString()
        }

        if (users.size > 2) {
            root.findViewById<TextView>(R.id.thirdName).text = users[2].username
            root.findViewById<TextView>(R.id.thirdScore).text = users[2].score.toString()
        }
    }

    private fun updateRecyclerView(users: List<LeaderboardUser>) {
        adapter.submitList(users)
    }
}