package com.example.cribswap.data.repo

import com.example.cribswap.data.model.Listing
import com.example.cribswap.ui.filter.FilterState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.math.*

class SubleaseRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val listingsCollection = db.collection("listings")

    fun getFilteredListings(
        filters: FilterState,
        userLatitude: Double? = null,
        userLongitude: Double? = null
    ): Flow<Result<List<Listing>>> = callbackFlow {
        try {
            val query = buildFirestoreQuery(filters)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val serverFilteredListings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()

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

    private fun buildFirestoreQuery(filters: FilterState): Query {
        var query: Query = listingsCollection
            .whereEqualTo("isActive", true)
        if (filters.priceMin > 0f) {
            query = query.whereGreaterThanOrEqualTo("rent", filters.priceMin.toDouble())
        }
        if (filters.priceMax < 5000f) {
            query = query.whereLessThanOrEqualTo("rent", filters.priceMax.toDouble())
        }
        if (filters.furnished) {
            query = query.whereEqualTo("isFurnished", true)
        }
        if (filters.bedrooms.isNotEmpty()) {
            query = query.whereIn("bedrooms", filters.bedrooms)
        }
        filters.getLeaseStartTimestamp()?.let { startTimestamp ->
            query = query.whereGreaterThanOrEqualTo("leaseStart", startTimestamp)
        }
        query = query.orderBy("createdAt", Query.Direction.DESCENDING)

        return query
    }

    private fun applyClientSideFilters(
        listings: List<Listing>,
        filters: FilterState,
        userLatitude: Double?,
        userLongitude: Double?
    ): List<Listing> {
        return listings.filter { listing ->
            val matchesBathrooms = if (filters.bathrooms.isNotEmpty()) {
                filters.bathrooms.contains(listing.bathrooms)
            } else true

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

            val matchesLeaseDates = if (
                filters.leaseStartMonth != null ||
                filters.leaseEndMonth != null
            ) {
                checkLeaseDateOverlap(listing, filters)
            } else true
            val matchesPhotos = if (filters.photosRequired) {
                listing.photoUrls.isNotEmpty()
            } else true

            matchesBathrooms &&
                    matchesDistance &&
                    matchesLeaseDates &&
                    matchesPhotos
        }
    }

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

    private fun checkLeaseDateOverlap(
        listing: Listing,
        filters: FilterState
    ): Boolean {
        val listingStart = listing.leaseStart ?: return true
        val listingEnd = listing.leaseEnd ?: return true

        val desiredStart = filters.getLeaseStartTimestamp()
        val desiredEnd = filters.getLeaseEndTimestamp()

        if (desiredStart != null) {
            if (listingStart.toDate().after(desiredStart.toDate())) {
                return false
            }
            if (listingEnd.toDate().before(desiredStart.toDate())) {
                return false
            }
        }

        if (desiredEnd != null) {

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