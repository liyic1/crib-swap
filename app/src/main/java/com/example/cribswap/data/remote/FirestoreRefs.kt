package com.example.cribswap.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreRefs {

    private val db get() = FirebaseFirestore.getInstance()

    // ── Top-level collections ─────────────────────────────────────────────────
    val listings      get() = db.collection("listings")
    val users         get() = db.collection("users")
    val conversations get() = db.collection("conversations")

    // ── Sub-collection helpers ────────────────────────────────────────────────
    fun messages(conversationId: String): CollectionReference =
        conversations.document(conversationId).collection("messages")

    fun savedListings(userId: String): CollectionReference =
        users.document(userId).collection("savedListings")

    // ── Document helpers ──────────────────────────────────────────────────────
    fun listing(listingId: String) = listings.document(listingId)
    fun user(userId: String)       = users.document(userId)
}
