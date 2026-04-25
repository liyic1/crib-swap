package com.example.cribswap.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
)