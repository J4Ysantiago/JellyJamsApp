package com.example.jellyjamsapp


import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    private val adapter = FriendsAdapter()
    private var friendsListener: ListenerRegistration? = null
    private var userListener: ListenerRegistration? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.friendsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val addFab = view.findViewById<FloatingActionButton>(R.id.addFriendFab)
        addFab.setOnClickListener {
            showAddFriendDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        attachFriendsListener()
    }

    override fun onStop() {
        super.onStop()
        friendsListener?.remove()
        userListener?.remove()
    }

    private fun attachFriendsListener() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Listen to changes in current user's friends array
        userListener = db.collection("users")
            .document(uid)
            .addSnapshotListener { userSnapshot, _ ->

                val friendIds = userSnapshot?.get("friends") as? List<String> ?: emptyList()

                if (friendIds.isEmpty()) {
                    adapter.submitList(emptyList())
                    friendsListener?.remove()
                    return@addSnapshotListener
                }

                friendsListener?.remove()

                friendsListener = db.collection("users")
                    .whereIn(FieldPath.documentId(), friendIds)
                    .addSnapshotListener { snapshot, error ->

                        if (error != null || snapshot == null) return@addSnapshotListener

                        val friends = snapshot.documents.mapNotNull { doc ->
                            val username = doc.getString("username") ?: return@mapNotNull null
                            val score = (doc.get("score") as? Number)?.toLong() ?: 0L

                            FriendUser(
                                userId = doc.id,
                                username = username,
                                score = score
                            )
                        }.sortedByDescending { it.score }

                        adapter.submitList(friends)
                    }
            }
    }

    private fun showAddFriendDialog() {
        val context = requireContext()
        val input = EditText(context)
        input.hint = "Enter Friend UID"

        AlertDialog.Builder(context)
            .setTitle("Add Friend 💜")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val friendUid = input.text.toString().trim()
                if (friendUid.isNotEmpty()) {
                    addFriend(friendUid)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addFriend(friendUid: String) {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        if (friendUid == currentUid) {
            Toast.makeText(requireContext(), "You can't add yourself 😅", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUserRef = db.collection("users").document(currentUid)
        val friendUserRef = db.collection("users").document(friendUid)

        db.runBatch { batch ->
            batch.update(currentUserRef, "friends", FieldValue.arrayUnion(friendUid))
            batch.update(friendUserRef, "friends", FieldValue.arrayUnion(currentUid))
        }.addOnSuccessListener {
            Toast.makeText(requireContext(), "Friend added 💜", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }
}