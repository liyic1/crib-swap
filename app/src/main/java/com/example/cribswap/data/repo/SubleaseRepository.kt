package com.example.cribswap.data.repo

import com.example.cribswap.data.model.Listing
import com.example.cribswap.ui.filter.FilterState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.math.*

/**
 * Repository for filtered sublease listings.
 *
 * ARCHITECTURE:
 * - Firestore handles: price, bedrooms, furnished (server-side)
 * - Client-side handles: bathrooms, location/distance, lease dates, photos (post-fetch)
 *
 * Why hybrid?
 * - Firestore allows only ONE whereIn() per query
 * - Distance filtering requires lat/long calculations
 * - Date overlap logic is complex for Firestore queries
 */
class SubleaseRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val listingsCollection = db.collection("listings")

    /**
     * Get filtered listings with real-time updates.
     *
     * Returns a Flow that emits new results whenever:
     * - Listings are added/updated/deleted in Firestore
     * - Filters change
     *
     * @param filters Current filter criteria
     * @param userLatitude User's current latitude (for distance filtering)
     * @param userLongitude User's current longitude (for distance filtering)
     */
    fun getFilteredListings(
        filters: FilterState,
        userLatitude: Double? = null,
        userLongitude: Double? = null
    ): Flow<Result<List<Listing>>> = callbackFlow {
        try {
            // Build optimized Firestore query
            val query = buildFirestoreQuery(filters)

            // Listen to real-time updates
            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val serverFilteredListings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()

                // Apply client-side filters
                val fullyFilteredListings = applyClientSideFilters(
                    listings = serverFilteredListings,
                    filters = filters,
                    userLatitude = userLatitude,
                    userLongitude = userLongitude
                )

                trySend(Result.success(fullyFilteredListings))
            }

            awaitClose { listener.remove() }

        } catch (e: Exception) {
            trySend(Result.failure(e))
            close(e)
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    /**
     * Build Firestore query with supported filters.
     *
     * CONSTRAINTS:
     * - Only ONE whereIn() allowed per query
     * - Range filters (price) use one inequality operator
     * - Boolean filters are simple equality checks
     */
    private fun buildFirestoreQuery(filters: FilterState): Query {
        var query: Query = listingsCollection
            .whereEqualTo("isActive", true)

        // ── Price Range ──────────────────────────────────────────────────────
        if (filters.priceMin > 0f) {
            query = query.whereGreaterThanOrEqualTo("rent", filters.priceMin.toDouble())
        }
        if (filters.priceMax < 5000f) {
            query = query.whereLessThanOrEqualTo("rent", filters.priceMax.toDouble())
        }

        // ── Furnished ────────────────────────────────────────────────────────
        if (filters.furnished) {
            query = query.whereEqualTo("isFurnished", true)
        }

        // ── Bedrooms (ONE whereIn allowed) ───────────────────────────────────
        // Priority: bedrooms > bathrooms because bedroom selection is more common
        if (filters.bedrooms.isNotEmpty()) {
            query = query.whereIn("bedrooms", filters.bedrooms)
        }

        // ── Lease Start Date (if specific month selected) ────────────────────
        // Only filter if user selected a specific start month
        filters.getLeaseStartTimestamp()?.let { startTimestamp ->
            query = query.whereGreaterThanOrEqualTo("leaseStart", startTimestamp)
        }

        // Order by creation date (newest first)
        query = query.orderBy("createdAt", Query.Direction.DESCENDING)

        return query
    }

    /**
     * Apply filters that can't be done efficiently in Firestore.
     *
     * CLIENT-SIDE FILTERS:
     * - Bathrooms (can't use 2nd whereIn)
     * - Distance from user location
     * - Lease date overlap validation
     * - Photos required check
     * - Future: roommates, parking, laundry (when added to Listing model)
     */
    private fun applyClientSideFilters(
        listings: List<Listing>,
        filters: FilterState,
        userLatitude: Double?,
        userLongitude: Double?
    ): List<Listing> {
        return listings.filter { listing ->
            // ── Bathrooms ────────────────────────────────────────────────────
            val matchesBathrooms = if (filters.bathrooms.isNotEmpty()) {
                filters.bathrooms.contains(listing.bathrooms)
            } else true

            // ── Distance ─────────────────────────────────────────────────────
            val matchesDistance = if (
                filters.locationQuery.isNotEmpty() &&
                userLatitude != null &&
                userLongitude != null &&
                listing.latitude != null &&
                listing.longitude != null
            ) {
                val distance = calculateDistance(
                    userLatitude, userLongitude,
                    listing.latitude, listing.longitude
                )
                distance <= filters.distanceMiles
            } else true

            // ── Lease Dates ──────────────────────────────────────────────────
            val matchesLeaseDates = if (
                filters.leaseStartMonth != null ||
                filters.leaseEndMonth != null
            ) {
                checkLeaseDateOverlap(listing, filters)
            } else true

            // ── Photos Required ──────────────────────────────────────────────
            val matchesPhotos = if (filters.photosRequired) {
                listing.photoUrls.isNotEmpty()
            } else true

            // ── Combine all conditions ───────────────────────────────────────
            matchesBathrooms &&
                    matchesDistance &&
                    matchesLeaseDates &&
                    matchesPhotos
        }
    }

    /**
     * Calculate distance between two lat/long points using Haversine formula.
     * Returns distance in miles.
     */
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusMiles = 3958.8

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusMiles * c
    }

    /**
     * Check if listing's lease period overlaps with user's desired dates.
     *
     * Logic:
     * - If user specifies start date: listing must START on or before desired start
     * - If user specifies end date: listing must END on or after desired end
     * - This ensures the listing covers the user's entire desired lease period
     */
    private fun checkLeaseDateOverlap(
        listing: Listing,
        filters: FilterState
    ): Boolean {
        val listingStart = listing.leaseStart ?: return true
        val listingEnd = listing.leaseEnd ?: return true

        val desiredStart = filters.getLeaseStartTimestamp()
        val desiredEnd = filters.getLeaseEndTimestamp()

        // If user wants to start on a specific date, listing must be available then
        if (desiredStart != null) {
            // Listing should start on or before desired start, and end after it
            if (listingStart.toDate().after(desiredStart.toDate())) {
                return false
            }
            if (listingEnd.toDate().before(desiredStart.toDate())) {
                return false
            }
        }

        // If user wants to end on a specific date, listing must cover that period
        if (desiredEnd != null) {
            // Listing should start before desired end, and end on or after it
            if (listingStart.toDate().after(desiredEnd.toDate())) {
                return false
            }
            if (listingEnd.toDate().before(desiredEnd.toDate())) {
                return false
            }
        }

        return true
    }
}