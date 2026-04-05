package com.example.cribswap.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Helper function that returns the correct firebase reference
 * prevents string typos and keeps all the paths in one place
 */
object FirestoreRefs {

    fun conversations(db: FirebaseFirestore): CollectionReference {
        return db.collection("conversations")
    }

    fun messages(
        db: FirebaseFirestore,
        conversationId: String
    ): CollectionReference {
        return conversations(db)
            .document(conversationId)
            .collection("messages")
    }
}