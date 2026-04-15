package com.example.cribswap.data.remote

import com.google.firebase.firestore.FirebaseFirestore

/**
 * Single source of truth for every Firestore collection path.
 * Import this object wherever you need a collection reference so
 * typos in collection names only have one place to fix.
 */
object FirestoreRefs {

    private val db get() = FirebaseFirestore.getInstance()

    // ── Top-level collections ─────────────────────────────────────────────────
    val listings    get() = db.collection("listings")
    val users       get() = db.collection("users")
    val conversations get() = db.collection("conversations")

    // ── Sub-collection helpers ────────────────────────────────────────────────

    /** Messages inside a conversation document. */
    fun messages(conversationId: String) =
        conversations.document(conversationId).collection("messages")

    /** Saved listings stored under a user document. */
    fun savedListings(userId: String) =
        users.document(userId).collection("savedListings")

    // ── Document helpers ──────────────────────────────────────────────────────

    fun listing(listingId: String) = listings.document(listingId)
    fun user(userId: String)       = users.document(userId)
}