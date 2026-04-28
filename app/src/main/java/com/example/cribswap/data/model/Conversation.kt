package com.example.cribswap.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/*
Shape of conversation document in fireStore
*/
data class Conversation(
    @DocumentId val id: String = "",
    val participants: List<String> = emptyList(),
    val listingId: String? = null,
    val otherUserName: String? = null,
    val lastMessage: String? = "",
    val lastMessageAt: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now()
)