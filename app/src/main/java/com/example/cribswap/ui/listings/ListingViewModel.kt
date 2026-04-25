package com.example.cribswap.ui.listings

import androidx.lifecycle.ViewModel
import com.example.cribswap.data.model.Listing
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*

// ── UI State ──────────────────────────────────────────────────────────────────

sealed class ListingUiState {
    object Loading : ListingUiState()
    data class Success(val listings: List<Listing>) : ListingUiState()
    data class Error(val message: String) : ListingUiState()
}

data class CreateListingFormState(
    val title: String = "",
    val description: String = "",
    val rent: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val bedrooms: String = "1",
    val bathrooms: String = "1",
    val squareFeet: String = "",
    val isFurnished: Boolean = false,
    val petsAllowed: Boolean = false,
    val utilitiesIncluded: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)

// ── Mock Data with real Unsplash apartment photos ─────────────────────────────

private val MOCK_LISTINGS = mutableListOf(
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

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ListingViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _feedState = MutableStateFlow<ListingUiState>(ListingUiState.Loading)
    val feedState: StateFlow<ListingUiState> = _feedState.asStateFlow()

    private val _maxRentFilter = MutableStateFlow<Double?>(null)
    val maxRentFilter: StateFlow<Double?> = _maxRentFilter.asStateFlow()

    private val _bedroomsFilter = MutableStateFlow<Int?>(null)
    val bedroomsFilter: StateFlow<Int?> = _bedroomsFilter.asStateFlow()

    private val _selectedListing = MutableStateFlow<Listing?>(null)
    val selectedListing: StateFlow<Listing?> = _selectedListing.asStateFlow()

    private val _formState = MutableStateFlow(CreateListingFormState())
    val formState: StateFlow<CreateListingFormState> = _formState.asStateFlow()

    init { loadListings() }

    fun loadListings() {
        _feedState.value = ListingUiState.Success(MOCK_LISTINGS.toList())
    }

    fun loadMyListings() {
        val myListings = MOCK_LISTINGS.filter { it.userId == "test-user" }
        _feedState.value = ListingUiState.Success(myListings)
    }

    fun applyFilters(maxRent: Double?, bedrooms: Int?) {
        _maxRentFilter.value = maxRent
        _bedroomsFilter.value = bedrooms
        val filtered = MOCK_LISTINGS.filter { listing ->
            (maxRent == null || listing.rent <= maxRent) &&
                    (bedrooms == null || listing.bedrooms == bedrooms)
        }
        _feedState.value = ListingUiState.Success(filtered)
    }

    fun clearFilters() {
        _maxRentFilter.value = null
        _bedroomsFilter.value = null
        _feedState.value = ListingUiState.Success(MOCK_LISTINGS.toList())
    }

    fun selectListing(listing: Listing) { _selectedListing.value = listing }
    fun clearSelectedListing() { _selectedListing.value = null }
    fun loadListingById(id: String) {
        _selectedListing.value = MOCK_LISTINGS.find { it.id == id }
    }

    fun deactivateListing(listingId: String) {
        MOCK_LISTINGS.removeAll { it.id == listingId }
        loadListings()
    }

    fun onTitleChange(v: String)       { _formState.update { it.copy(title = v) } }
    fun onDescriptionChange(v: String) { _formState.update { it.copy(description = v) } }
    fun onRentChange(v: String)        { _formState.update { it.copy(rent = v) } }
    fun onAddressChange(v: String)     { _formState.update { it.copy(address = v) } }
    fun onCityChange(v: String)        { _formState.update { it.copy(city = v) } }
    fun onStateChange(v: String)       { _formState.update { it.copy(state = v) } }
    fun onZipChange(v: String)         { _formState.update { it.copy(zipCode = v) } }
    fun onBedroomsChange(v: String)    { _formState.update { it.copy(bedrooms = v) } }
    fun onBathroomsChange(v: String)   { _formState.update { it.copy(bathrooms = v) } }
    fun onSqFtChange(v: String)        { _formState.update { it.copy(squareFeet = v) } }
    fun onFurnishedToggle()            { _formState.update { it.copy(isFurnished = !it.isFurnished) } }
    fun onPetsToggle()                 { _formState.update { it.copy(petsAllowed = !it.petsAllowed) } }
    fun onUtilitiesToggle()            { _formState.update { it.copy(utilitiesIncluded = !it.utilitiesIncluded) } }

    fun submitListing() {
        val form = _formState.value

        if (form.title.isBlank()) {
            _formState.update { it.copy(errorMessage = "Please enter a listing title.") }
            return
        }
        val rentDouble = form.rent.toDoubleOrNull() ?: run {
            _formState.update { it.copy(errorMessage = "Please enter a valid rent amount.") }
            return
        }
        if (rentDouble <= 0) {
            _formState.update { it.copy(errorMessage = "Rent must be greater than \$0.") }
            return
        }
        if (form.city.isBlank()) {
            _formState.update { it.copy(errorMessage = "Please enter a city.") }
            return
        }

        _formState.update { it.copy(isSubmitting = true, errorMessage = null) }

        val newListing = Listing(
            id                = "new-${System.currentTimeMillis()}",
            userId            = auth.currentUser?.uid ?: "test-user",
            ownerName         = auth.currentUser?.displayName ?: "Sarah (Me)",
            ownerPhotoUrl     = auth.currentUser?.photoUrl?.toString(),
            title             = form.title.trim(),
            description       = form.description.trim(),
            rent              = rentDouble,
            address           = form.address.trim(),
            city              = form.city.trim(),
            state             = form.state.trim(),
            zipCode           = form.zipCode.trim(),
            bedrooms          = form.bedrooms.toIntOrNull() ?: 1,
            bathrooms         = form.bathrooms.toDoubleOrNull() ?: 1.0,
            squareFeet        = form.squareFeet.toIntOrNull(),
            isFurnished       = form.isFurnished,
            petsAllowed       = form.petsAllowed,
            utilitiesIncluded = form.utilitiesIncluded,
            createdAt         = Timestamp.now(),
            isActive          = true,
            photoUrls         = listOf("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&q=80")
        )

        MOCK_LISTINGS.add(0, newListing)
        _feedState.value = ListingUiState.Success(MOCK_LISTINGS.toList())
        _formState.value = CreateListingFormState(submitSuccess = true)
    }

    fun resetForm() { _formState.value = CreateListingFormState() }
}