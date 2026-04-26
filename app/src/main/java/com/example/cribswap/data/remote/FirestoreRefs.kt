package com.example.cribswap.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Single source of truth for all Firestore collection paths.
 *
 * USAGE:
 *   val listingsRef = FirestoreRefs.listings()
 *   val userRef = FirestoreRefs.user(userId)
 *
 * Prevents typos and keeps all paths centralized.
 */
object FirestoreRefs {

    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    // ── Top-level Collections ────────────────────────────────────────────────

    fun listings(): CollectionReference =
        db.collection("listings")

    fun users(): CollectionReference =
        db.collection("users")

    fun conversations(): CollectionReference =
        db.collection("conversations")

    // ── Document References ──────────────────────────────────────────────────

    fun listing(listingId: String): DocumentReference =
        listings().document(listingId)

    fun user(userId: String): DocumentReference =
        users().document(userId)

    fun conversation(conversationId: String): DocumentReference =
        conversations().document(conversationId)

    // ── Sub-collections ──────────────────────────────────────────────────────

    /**
     * Messages inside a specific conversation.
     */
    fun messages(conversationId: String): CollectionReference =
        conversation(conversationId).collection("messages")

    /**
     * Saved/favorited listings for a specific user.
     */
    fun savedListings(userId: String): CollectionReference =
        user(userId).collection("savedListings")

    // ── Backward Compatibility (deprecated, use methods above) ──────────────

    @Deprecated("Use listings() instead", ReplaceWith("listings()"))
    fun conversations(db: FirebaseFirestore): CollectionReference =
        db.collection("conversations")

    @Deprecated("Use messages(conversationId) instead", ReplaceWith("messages(conversationId)"))
    fun messages(db: FirebaseFirestore, conversationId: String): CollectionReference =
        messages(conversationId)
}