package com.example.jellyjamsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FriendsAdapter :
    ListAdapter<FriendUser, FriendsAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.friendUsername)
        val score: TextView = view.findViewById(R.id.friendScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = getItem(position)
        holder.username.text = friend.username
        holder.score.text = "Score: ${friend.score}"

        val initial = friend.username.first().uppercase()
        holder.itemView.findViewById<TextView>(R.id.friendInitial).text = initial
        holder.score.text = "${friend.score} pts"
    }

    class DiffCallback : DiffUtil.ItemCallback<FriendUser>() {
        override fun areItemsTheSame(old: FriendUser, new: FriendUser) =
            old.userId == new.userId

        override fun areContentsTheSame(old: FriendUser, new: FriendUser) =
            old == new
    }
}

