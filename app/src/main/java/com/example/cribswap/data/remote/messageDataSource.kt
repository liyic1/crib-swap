package com.example.cribswap.data.remote

import com.example.cribswap.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class MessageRemoteDataSource(
    private val db: FirebaseFirestore
) {

    suspend fun sendMessage(message: Message) {
        val docRef = FirestoreRefs
            .messages(db, message.conversationId)
            .document()

        val messageWithId = message.copy(id = docRef.id)

        docRef.set(messageWithId).await()
    }

    suspend fun getMessagesForConversation(conversationId: String): List<Message> {
        return FirestoreRefs
            .messages(db, conversationId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                doc.toObject(Message::class.java)
            }
    }
}
