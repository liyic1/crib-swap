package com.example.cribswap.ui.listings

import androidx.lifecycle.ViewModel
import com.example.cribswap.data.model.Listing
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import com.example.cribswap.data.MockListingData

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

    // ── Saved Listings ────────────────────────────────────────────────────────
    private val _savedListingIds = MutableStateFlow<Set<String>>(emptySet())
    val savedListingIds: StateFlow<Set<String>> = _savedListingIds.asStateFlow()

    init { loadListings() }

    // ── Feed ──────────────────────────────────────────────────────────────────

    fun loadListings() {
        _feedState.value = ListingUiState.Success(MockListingData.MOCK_LISTINGS.toList())
    }

    fun loadMyListings() {
        val myListings = MockListingData.MOCK_LISTINGS.filter { it.userId == "test-user" }
        _feedState.value = ListingUiState.Success(myListings)
    }

    // ── Saved ─────────────────────────────────────────────────────────────────

    fun toggleSaved(listing: Listing) {
        _savedListingIds.update { current ->
            if (listing.id in current) current - listing.id
            else current + listing.id
        }
    }

    fun isListingSaved(listingId: String): Boolean {
        return listingId in _savedListingIds.value
    }

    fun getSavedListings(): List<Listing> {
        return MockListingData.MOCK_LISTINGS.filter { it.id in _savedListingIds.value }
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    fun applyFilters(maxRent: Double?, bedrooms: Int?) {
        _maxRentFilter.value = maxRent
        _bedroomsFilter.value = bedrooms
        val filtered = MockListingData.MOCK_LISTINGS.filter { listing ->
            (maxRent == null || listing.rent <= maxRent) &&
                    (bedrooms == null || listing.bedrooms == bedrooms)
        }
        _feedState.value = ListingUiState.Success(filtered)
    }

    fun clearFilters() {
        _maxRentFilter.value = null
        _bedroomsFilter.value = null
        _feedState.value = ListingUiState.Success(MockListingData.MOCK_LISTINGS.toList())
    }

    // ── Detail ────────────────────────────────────────────────────────────────

    fun selectListing(listing: Listing) { _selectedListing.value = listing }
    fun clearSelectedListing() { _selectedListing.value = null }
    fun loadListingById(id: String) {
        _selectedListing.value = MockListingData.MOCK_LISTINGS.find { it.id == id }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    fun deactivateListing(listingId: String) {
        MockListingData.MOCK_LISTINGS.removeAll { it.id == listingId }
        loadListings()
    }

    // ── Create form handlers ──────────────────────────────────────────────────

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

    // ── Submit ────────────────────────────────────────────────────────────────

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

        MockListingData.MOCK_LISTINGS.add(0, newListing)
        _feedState.value = ListingUiState.Success(MockListingData.MOCK_LISTINGS.toList())
        _formState.value = CreateListingFormState(submitSuccess = true)
    }

    fun resetForm() { _formState.value = CreateListingFormState() }
}