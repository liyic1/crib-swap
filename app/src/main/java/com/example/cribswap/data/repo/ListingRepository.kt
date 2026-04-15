package com.example.cribswap.data.repo

import com.example.cribswap.data.model.Listing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ListingRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val listingsCollection = db.collection("listings")

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Live stream of ALL active listings, newest first. */
    fun getAllListings(): Flow<List<Listing>> = callbackFlow {
        val listener = listingsCollection
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val listings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }

    /** Single listing by document ID. */
    suspend fun getListingById(listingId: String): Listing? =
        listingsCollection.document(listingId)
            .get()
            .await()
            .toObject(Listing::class.java)

    /** Listings posted by a specific user. */
    fun getListingsByUser(userId: String): Flow<List<Listing>> = callbackFlow {
        val listener = listingsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val listings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }

    // ── Filter ────────────────────────────────────────────────────────────────

    /** Filter by max rent and/or number of bedrooms. */
    fun getFilteredListings(maxRent: Double?, bedrooms: Int?): Flow<List<Listing>> =
        callbackFlow {
            var query: Query = listingsCollection.whereEqualTo("isActive", true)
            if (maxRent != null) query = query.whereLessThanOrEqualTo("rent", maxRent)
            if (bedrooms != null) query = query.whereEqualTo("bedrooms", bedrooms)
            query = query.orderBy("createdAt", Query.Direction.DESCENDING)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val listings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()
                trySend(listings)
            }
            awaitClose { listener.remove() }
        }

    // ── Write ─────────────────────────────────────────────────────────────────

    /** Create a new listing. Returns the generated document ID. */
    suspend fun createListing(listing: Listing): String {
        val ref = listingsCollection.document()
        ref.set(listing.copy(id = ref.id)).await()
        return ref.id
    }

    /** Update an existing listing (full replace). */
    suspend fun updateListing(listing: Listing) {
        listingsCollection.document(listing.id).set(listing).await()
    }

    /** Soft-delete: mark isActive = false instead of deleting. */
    suspend fun deactivateListing(listingId: String) {
        listingsCollection.document(listingId)
            .update("isActive", false)
            .await()
    }
}