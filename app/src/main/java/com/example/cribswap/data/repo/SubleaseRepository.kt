package com.example.cribswap.data.repo

import com.example.cribswap.ui.filter.FilterState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class SubleaseRepository(private val db: FirebaseFirestore) {
    private val _activeFilters = MutableStateFlow(FilterState())
    val activeFilters: StateFlow<FilterState> = _activeFilters.asStateFlow()

    fun updateFilters(newState: FilterState) {
        _activeFilters.value = newState
    }

    fun getFilteredListingsQuery(filters: FilterState): Query {
        var query: Query = db.collection("listings")
        if (filters.priceMin > 0f) {
            query = query.whereGreaterThanOrEqualTo("price", filters.priceMin)
        }
        if (filters.priceMax < 5000f) { //Only filter if it's below max slider value
            query = query.whereLessThanOrEqualTo("price", filters.priceMax)
        }

        if (filters.furnished) query = query.whereEqualTo("isFurnished", true)
        if (filters.inUnitLaundry) query = query.whereEqualTo("hasLaundry", true)
        if (filters.parking) query = query.whereEqualTo("hasParking", true)

        if (filters.bedrooms.isNotEmpty()) {
            query = query.whereIn("numBedrooms", filters.bedrooms)
        }
        if (filters.buildingTypes.isNotEmpty()) {
            query = query.whereIn("propertyType", filters.buildingTypes)
        }

        // data logic for lease term: convert month/year to timestamp here

        return query
    }

}