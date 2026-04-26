package com.example.cribswap.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cribswap.data.model.Listing
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.*
import java.util.Calendar
import com.example.cribswap.data.MockListingData

class FilterViewModel : ViewModel() {

    private val currentYear: Int
        get() = Calendar.getInstance().get(Calendar.YEAR)

    private val defaultState: FilterState
        get() = FilterState(
            leaseStartYear = currentYear,
            leaseEndYear = currentYear
        )

    private val _draft = MutableStateFlow(defaultState)
    val draft: StateFlow<FilterState> = _draft.asStateFlow()

    private val _applied = MutableStateFlow(defaultState)
    val applied: StateFlow<FilterState> = _applied.asStateFlow()

    val filteredListings: StateFlow<Result<List<Listing>>> = _applied
        .map { filters ->
            try {
                val filtered = applyMockFilters(MockListingData.MOCK_LISTINGS, filters)
                Result.success(filtered)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.success(MockListingData.MOCK_LISTINGS)
        )

    private fun applyMockFilters(listings: List<Listing>, filters: FilterState): List<Listing> {
        return listings.filter { listing ->
            // Price range
            val matchesPrice = listing.rent >= filters.priceMin && listing.rent <= filters.priceMax

            // Bedrooms
            val matchesBedrooms = if (filters.bedrooms.isNotEmpty()) {
                filters.bedrooms.contains(listing.bedrooms)
            } else true

            // Bathrooms
            val matchesBathrooms = if (filters.bathrooms.isNotEmpty()) {
                filters.bathrooms.contains(listing.bathrooms)
            } else true

            // Furnished
            val matchesFurnished = if (filters.furnished) {
                listing.isFurnished
            } else true

            // Photos required
            val matchesPhotos = if (filters.photosRequired) {
                listing.photoUrls.isNotEmpty()
            } else true

            matchesPrice && matchesBedrooms && matchesBathrooms && matchesFurnished && matchesPhotos
        }
    }

    fun updateDraft(update: FilterState.() -> FilterState) {
        _draft.value = _draft.value.update()
    }

    fun applyFilters() {
        _applied.value = _draft.value
    }

    fun resetFilters() {
        _draft.value = defaultState
        _applied.value = defaultState
    }

    fun applyPreferences(state: FilterState) {
        _draft.value = state
        _applied.value = state
    }

    fun toggleBedroom(bedroomString: String) {
        val bedroomInt = BedroomMapper.stringToInt(bedroomString)
        updateDraft {
            copy(bedrooms = bedrooms.toggle(bedroomInt))
        }
    }

    fun toggleBathroom(bathroomString: String) {
        val bathroomDouble = BathroomMapper.stringToDouble(bathroomString)
        updateDraft {
            copy(bathrooms = bathrooms.toggle(bathroomDouble))
        }
    }

}

private fun <T> List<T>.toggle(item: T): List<T> =
    if (contains(item)) this - item else this + item
