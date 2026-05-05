package com.example.jellyjamsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FriendsAdapter(
    private val onRemoveClick: (FriendUser) -> Unit
) : ListAdapter<FriendUser, FriendsAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.friendUsername)

        val mood: TextView = view.findViewById(R.id.friendScore)
        val initial: TextView = view.findViewById(R.id.friendInitial)
        val song: TextView = view.findViewById(R.id.friendSong)
        val rank: TextView = view.findViewById(R.id.friendRank)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = getItem(position)

        holder.username.text = friend.username

        holder.mood.text = "Feeling ${friend.currentMood} "

        holder.initial.text = friend.username.first().toString()

        holder.song.text = "🎧 ${friend.currentSong}"

        holder.rank.text = "#${position + 1}"

        // Tap
        holder.itemView.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Viewing ${friend.username}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Long press to remove
        holder.itemView.setOnLongClickListener {
            onRemoveClick(friend)
            true
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FriendUser>() {
        override fun areItemsTheSame(old: FriendUser, new: FriendUser) =
            old.userId == new.userId

        override fun areContentsTheSame(old: FriendUser, new: FriendUser) =
            old == new
    }
}