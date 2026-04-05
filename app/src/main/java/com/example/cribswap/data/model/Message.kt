package com.example.cribswap.data.model

data class Message(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)
