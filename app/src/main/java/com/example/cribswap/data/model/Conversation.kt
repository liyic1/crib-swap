package com.example.cribswap.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/*
Shape of conversation document in fireStore

*/
data class Conversation(
    // FireStore Document id. Tells firestore to store document id's here when a new one is created.
    @DocumentId val id: String = "",
    //Store list of userId in on conversation, Ex. [myUserId, otherUserId]
    val participants: List<String> = emptyList(),
    //Store the latest message, so we don't have to fetch it everytime
    val lastMessage: String? = "",
    // time of the last message
    val lastMessageAt: Timestamp? = null,
    //when the conversation was created
    val createdAt: Timestamp? = null
    // set everything as a default to prevent crashes like when a field doesn't exist
)
