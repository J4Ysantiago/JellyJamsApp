package com.example.jellyjamsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class LeaderboardAdapter :
    ListAdapter<LeaderboardUser, LeaderboardAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.rankText)
        val username: TextView = view.findViewById(R.id.usernameText)
        val score: TextView = view.findViewById(R.id.scoreText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)

        holder.rank.text = when (position) {
            0 -> "🥇"
            1 -> "🥈"
            2 -> "🥉"
            else -> "#${position + 1}"
        }

        holder.username.text = user.username
        holder.score.text = user.score.toString()

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (user.userId == currentUserId) {
            holder.itemView.setBackgroundResource(R.color.current_user_highlight)
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent)
        }
    }




    class DiffCallback : DiffUtil.ItemCallback<LeaderboardUser>() {
        override fun areItemsTheSame(old: LeaderboardUser, new: LeaderboardUser) =
            old.userId == new.userId

        override fun areContentsTheSame(old: LeaderboardUser, new: LeaderboardUser) =
            old == new
    }
}