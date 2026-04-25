package com.example.cribswap.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Placeholder Listings
 */
data class Listing(
    @DocumentId val id: String = "",
    val title: String = "",
    val rent: Double = 0.0,
    val bedrooms: Int = 1,
    val bathrooms: Double = 1.0,
    val isFurnished: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val leaseStart: Timestamp? = null,
    val leaseEnd: Timestamp? = null,
    val photoUrls: List<String> = emptyList(),
    val createdAt: Timestamp? = null,
    val isActive: Boolean = true
)