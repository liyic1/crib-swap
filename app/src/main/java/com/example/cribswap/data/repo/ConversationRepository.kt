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
    suspend fun createConversation(otherUserId: String): String {
        //Get the current logged-in user's UID from FirebaseAuth
        val myUid = auth.currentUser?.uid ?: error("Not signed in")
        //Store participants as a stable sorted list.
        //Sorting helps avoid duplicates caused by different order.
        val participants = listOf(myUid, otherUserId).sorted()

        val existing = FirestoreRefs.conversations(db)
            .whereEqualTo("participants", participants)
            .get()
            .await()

        if (!existing.isEmpty) {
            return existing.documents.first().id
        }

        //Create a new document with an auto-generated ID
        val doc = FirestoreRefs.conversations(db).document()
        //Create the Conversation object that will be saved to Firestore
        val data = Conversation(
            id = doc.id,
            participants = participants,
            createdAt = Timestamp.now(),
            lastMessage = "",
            lastMessageAt = null
        )
        //Write the object to Firestore (wait until it finishes)
        doc.set(data).await()
        //return conversation id
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
        val myUid = auth.currentUser?.uid ?: error("not signed in")
        //Query the conversation collection
        val query = FirestoreRefs.conversations(db)
            .whereArrayContains("participants", myUid) //find convo containing this user
            .orderBy("createdAt") //Sort convo by creation time
            .get() //execute query
            .await() // Wait until fireStore responds

        //Convert Firestore Documents to Conversation Objects
        return query.documents.mapNotNull { doc ->
            doc.toObject(Conversation::class.java)
        }

    }
    suspend fun getConversationById(conversationId: String): Conversation? {
        val doc = FirestoreRefs.conversations(db)
            .document(conversationId)
            .get()
            .await()

        return doc.toObject(Conversation::class.java)
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
