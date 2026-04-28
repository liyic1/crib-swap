package com.example.cribswap.data.repo

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cribswap.data.model.Conversation
import com.example.cribswap.data.remote.FirestoreRefs
import kotlinx.coroutines.tasks.await

class ConversationRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getOrCreateConversation(
        otherUserId: String,
        listingId: String? = null,
        otherUserName: String? = null
    ): String {
        val myUid = auth.currentUser?.uid ?: "test-buyer-1"
        val participants = listOf(myUid, otherUserId).sorted()

        var query = FirestoreRefs.conversations(db)
            .whereEqualTo("participants", participants)

        if (listingId != null) {
            query = query.whereEqualTo("listingId", listingId)
        }

        val existing = query.get().await()

        if (!existing.isEmpty) {
            return existing.documents.first().id
        }

        val doc = FirestoreRefs.conversations(db).document()

        val data = Conversation(
            id = doc.id,
            participants = participants,
            listingId = listingId,
            otherUserName = otherUserName,
            createdAt = Timestamp.now(),
            lastMessage = "",
            lastMessageAt = null
        )

        doc.set(data).await()
        return doc.id
    }

    suspend fun getConversation(): List<Conversation> {
        val myUid = auth.currentUser?.uid ?: "test-buyer-1"
        val query = FirestoreRefs.conversations(db)
            .whereArrayContains("participants", myUid)
            .get()
            .await()

        return query.documents.mapNotNull { doc ->
            doc.toObject(Conversation::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun getConversationById(conversationId: String): Conversation? {
        val doc = FirestoreRefs.conversations(db)
            .document(conversationId)
            .get()
            .await()

        return doc.toObject(Conversation::class.java)?.copy(id = doc.id)
    }

    suspend fun updateLastMessage(
        conversationId: String,
        messageText: String
    ) {
        FirestoreRefs.conversations(db)
            .document(conversationId)
            .update(
                mapOf(
                    "lastMessage" to messageText,
                    "lastMessageAt" to Timestamp.now()
                )
            )
            .await()
    }
}