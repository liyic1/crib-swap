package com.example.cribswap.data.repo

import com.example.cribswap.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun createUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(user.uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Fetches a single user document from Firestore by their UID.
    fun getUser(uid: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener { doc -> onSuccess(doc.toObject(User::class.java)) }
            .addOnFailureListener { onFailure(it) }
    }

    // Updates specific fields of an existing user document.
    fun updateUser(uid: String, fields: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(uid)
            .update(fields)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}