package com.example.cribswap.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cribswap.data.model.Listing
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.*
import java.util.Calendar

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

    // 🔥 MODIFIED: Use mock data for now (swap with repository later)
    val filteredListings: StateFlow<Result<List<Listing>>> = _applied
        .map { filters ->
            try {
                val filtered = applyMockFilters(MOCK_LISTINGS, filters)
                Result.success(filtered)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.success(MOCK_LISTINGS)
        )

    // 🔥 TEMPORARY: Mock filtering logic (replace with repository when ready)
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
        _applied.value = defaultState  // 🔥 Also reset applied filters
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

    fun toggleRoommate(roommateString: String) {
        val roommateInt = when (roommateString) {
            "4+" -> 5
            else -> roommateString.toIntOrNull() ?: 0
        }
        updateDraft {
            copy(roommates = roommates.toggle(roommateInt))
        }
    }

    fun toggleBuildingType(type: String) {
        updateDraft {
            copy(buildingTypes = buildingTypes.toggle(type))
        }
    }
}

private fun <T> List<T>.toggle(item: T): List<T> =
    if (contains(item)) this - item else this + item

// 🔥 TEMPORARY: Copy mock data here (will be removed when using repository)
private val MOCK_LISTINGS = listOf(
    Listing(
        id = "sarah-listing-1",
        userId = "test-user",
        ownerName = "Sarah (Me)",
        title = "Cozy 2BR near U of M",
        description = "Bright corner unit a 10-min walk from campus. Hardwood floors, updated kitchen, in-unit laundry. Great natural light all day.",
        rent = 1100.0,
        address = "123 University Ave SE",
        city = "Minneapolis",
        state = "MN",
        zipCode = "55414",
        bedrooms = 2,
        bathrooms = 1.0,
        squareFeet = 820,
        isFurnished = false,
        petsAllowed = true,
        utilitiesIncluded = false,
        isActive = true,
        photoUrls = listOf("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&q=80")
    ),
    Listing(
        id = "listing-2",
        userId = "user-2",
        ownerName = "Priya S.",
        title = "Modern Studio — Cedar-Riverside",
        description = "Fully furnished studio in a high-rise. All utilities included. Steps from the light rail and campus shuttle stop.",
        rent = 750.0,
        address = "500 Cedar St",
        city = "Minneapolis",
        state = "MN",
        zipCode = "55454",
        bedrooms = 1,
        bathrooms = 1.0,
        squareFeet = 420,
        isFurnished = true,
        petsAllowed = false,
        utilitiesIncluded = true,
        isActive = true,
        photoUrls = listOf("https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800&q=80")
    ),
    Listing(
        id = "listing-3",
        userId = "user-3",
        ownerName = "Jordan M.",
        title = "Cozy 3BR House — Dinkytown",
        description = "Spacious house with a backyard, front porch, and dedicated parking for 2 cars. Perfect for a group of 3 students.",
        rent = 1500.0,
        address = "14th Ave SE",
        city = "Minneapolis",
        state = "MN",
        zipCode = "55414",
        bedrooms = 3,
        bathrooms = 2.0,
        squareFeet = 1200,
        isFurnished = false,
        petsAllowed = true,
        utilitiesIncluded = false,
        isActive = true,
        photoUrls = listOf("https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800&q=80")
    ),
    Listing(
        id = "listing-4",
        userId = "user-4",
        ownerName = "Sam T.",
        title = "1BR with Gym & Rooftop",
        description = "Modern building with a rooftop deck and fitness center. Quiet floor, fast WiFi in common areas, controlled entry.",
        rent = 995.0,
        address = "300 2nd Ave S",
        city = "Minneapolis",
        state = "MN",
        zipCode = "55401",
        bedrooms = 1,
        bathrooms = 1.0,
        squareFeet = 550,
        isFurnished = false,
        petsAllowed = false,
        utilitiesIncluded = false,
        isActive = true,
        photoUrls = listOf("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&q=80")
    ),
    Listing(
        id = "listing-5",
        userId = "user-5",
        ownerName = "Aisha K.",
        title = "Sunny 1BR — Uptown",
        description = "Charming 1BR in the heart of Uptown. Walk to restaurants, coffee shops, and Lake Calhoun.",
        rent = 850.0,
        address = "2900 Hennepin Ave",
        city = "Minneapolis",
        state = "MN",
        zipCode = "55408",
        bedrooms = 1,
        bathrooms = 1.0,
        squareFeet = 600,
        isFurnished = true,
        petsAllowed = false,
        utilitiesIncluded = true,
        isActive = true,
        photoUrls = listOf("https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800&q=80")
    ),
    Listing(
        id = "listing-6",
        userId = "user-6",
        ownerName = "Marcus W.",
        title = "Spacious 2BR — Northeast",
        description = "Renovated 2BR in the arts district. Exposed brick, open floor plan, rooftop access. Walking distance to breweries and cafes.",
        rent = 1250.0,
        address = "789 Central Ave NE",
        city = "Minneapolis",
        state = "MN",
        zipCode = "55413",
        bedrooms = 2,
        bathrooms = 1.0,
        squareFeet = 950,
        isFurnished = false,
        petsAllowed = true,
        utilitiesIncluded = false,
        isActive = true,
        photoUrls = listOf("https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&q=80")
    ),
    Listing(
        id = "listing-7",
        userId = "user-7",
        ownerName = "Lena P.",
        title = "Furnished Studio — Downtown",
        description = "Sleek fully furnished studio in the heart of downtown. Floor-to-ceiling windows with city views. Perfect for one person.",
        rent = 900.0,
        address = "100 Washington Ave S",
        city = "Minneapolis",
        state = "MN",
        zipCode = "55401",
        bedrooms = 1,
        bathrooms = 1.0,
        squareFeet = 480,
        isFurnished = true,
        petsAllowed = false,
        utilitiesIncluded = true,
        isActive = true,
        photoUrls = listOf("https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=800&q=80")
    )
)