package com.example.cribswap.data.repo

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cribswap.data.model.Conversation
import com.example.cribswap.data.remote.FirestoreRefs
import kotlinx.coroutines.tasks.await

/**
 * Creates a new conversation between the current signed-in user and otherUserId.
 * Returns the new conversationId (Firestore document ID).
 */
class ConversationRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getOrCreateConversation(
        otherUserId: String,
        listingId: String? = null
    ): String {
        // Get the current logged-in user's UID from FirebaseAuth.
        // Temporary fallback lets us test messaging before login is finished.
        val myUid = auth.currentUser?.uid ?: "test-buyer-1"

        // Store participants as a stable sorted list.
        // Sorting helps avoid duplicates caused by different order.
        val participants = listOf(myUid, otherUserId).sorted()

        var query = FirestoreRefs.conversations(db)
            .whereEqualTo("participants", participants)

        // If this conversation comes from a listing, also match by listingId.
        // This prevents different listings with the same seller from sharing one chat.
        if (listingId != null) {
            query = query.whereEqualTo("listingId", listingId)
        }

        val existing = query.get().await()

        if (!existing.isEmpty) {
            return existing.documents.first().id
        }

        // Create a new document with an auto-generated ID.
        val doc = FirestoreRefs.conversations(db).document()

        // Create the Conversation object that will be saved to Firestore.
        val data = Conversation(
            id = doc.id,
            participants = participants,
            listingId = listingId,
            createdAt = Timestamp.now(),
            lastMessage = "",
            lastMessageAt = null
        )

        // Write the object to Firestore and wait until it finishes.
        doc.set(data).await()

        // Return conversation id.
        return doc.id
    }

    /**
     * Return a list of conversation documents where the current user is the participant
     * gets the current user ID
     * searches firestore : Conversations collection, where par contains myUid
     * returns results
     */
    suspend fun getConversation(): List<Conversation>{
        // get current user
//        val myUid = auth.currentUser?.uid ?: error("not signed in")
        val myUid = auth.currentUser?.uid ?: "test-buyer-1"
        //Query the conversation collection
        val query = FirestoreRefs.conversations(db)
            .whereArrayContains("participants", myUid) //find convo containing this user
            //.orderBy("lastMessageAt") //Sort convo by creation time
            .get() //execute query
            .await() // Wait until fireStore responds

        //Convert Firestore Documents to Conversation Objects
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
