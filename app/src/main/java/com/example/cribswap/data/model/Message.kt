package com.example.cribswap.data.model
import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val read: Boolean = false
)