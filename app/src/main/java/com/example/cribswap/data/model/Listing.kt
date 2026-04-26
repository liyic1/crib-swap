package com.example.cribswap.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Shape of a Listing document stored in MongoDB (via REST) / Firestore.
 * All fields have defaults so partial documents don't crash the app.
 */
data class Listing(
    @DocumentId val id: String = "",

    // Owner
    val userId: String = "",          // Firebase UID of the poster
    val ownerName: String = "",
    val ownerPhotoUrl: String? = null,
    // Core listing info
    val title: String = "",
    val description: String = "",
    val rent: Double = 0.0,           // monthly rent in USD

    // Location
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,

    // Apartment details
    val bedrooms: Int = 1,
    val bathrooms: Double = 1.0,      // 1.0, 1.5, 2.0 …
    val squareFeet: Int? = null,
    val isFurnished: Boolean = false,
    val petsAllowed: Boolean = false,
    val utilitiesIncluded: Boolean = false,

    // Lease window
    val leaseStart: Timestamp? = null,
    val leaseEnd: Timestamp? = null,

    // Media
    val photoUrls: List<String> = emptyList(),

    // Metadata
    val createdAt: Timestamp? = null,
    val isActive: Boolean = true
)