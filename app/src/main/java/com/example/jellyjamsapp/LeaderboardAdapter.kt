package com.example.jellyjamsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class LeaderboardAdapter :
    ListAdapter<LeaderboardUser, LeaderboardAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LeaderboardUser>() {
            override fun areItemsTheSame(oldItem: LeaderboardUser, newItem: LeaderboardUser) =
                oldItem.username == newItem.username

            override fun areContentsTheSame(oldItem: LeaderboardUser, newItem: LeaderboardUser) =
                oldItem == newItem
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.usernameText)
        val score: TextView = view.findViewById(R.id.scoreText)
        val rank: TextView = view.findViewById(R.id.rankText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)

        holder.username.text = user.username
        holder.score.text = "${user.score} moods"
        holder.rank.text = "#${position + 4}" // because top 3 are in podium

        // subtle visual hierarchy
        holder.itemView.alpha = if (position == 0) 1f else 0.85f
    }
}