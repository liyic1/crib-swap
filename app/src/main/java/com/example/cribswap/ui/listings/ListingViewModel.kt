package com.example.cribswap.ui.listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cribswap.data.model.Listing
import com.example.cribswap.data.repo.ListingRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

class ListingViewModel(
    private val repo: ListingRepository = ListingRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

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

    // ── Feed ──────────────────────────────────────────────────────────────────

    fun loadListings() {
        viewModelScope.launch {
            _feedState.value = ListingUiState.Loading
            try {
                repo.getAllListings().collect { listings ->
                    _feedState.value = ListingUiState.Success(listings)
                }
            } catch (e: Exception) {
                _feedState.value = ListingUiState.Error(e.message ?: "Failed to load listings")
            }
        }
    }

    /** Load only the current signed-in user's listings (for My Listings screen). */
    fun loadMyListings() {
        val uid = auth.currentUser?.uid ?: run {
            _feedState.value = ListingUiState.Error("Not signed in")
            return
        }
        viewModelScope.launch {
            _feedState.value = ListingUiState.Loading
            try {
                repo.getListingsByUser(uid).collect { listings ->
                    _feedState.value = ListingUiState.Success(listings)
                }
            } catch (e: Exception) {
                _feedState.value = ListingUiState.Error(e.message ?: "Failed to load your listings")
            }
        }
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    fun applyFilters(maxRent: Double?, bedrooms: Int?) {
        _maxRentFilter.value = maxRent
        _bedroomsFilter.value = bedrooms
        viewModelScope.launch {
            _feedState.value = ListingUiState.Loading
            try {
                repo.getFilteredListings(maxRent, bedrooms).collect { listings ->
                    _feedState.value = ListingUiState.Success(listings)
                }
            } catch (e: Exception) {
                _feedState.value = ListingUiState.Error(e.message ?: "Filter failed")
            }
        }
    }

    fun clearFilters() = applyFilters(null, null)

    // ── Detail ────────────────────────────────────────────────────────────────

    fun selectListing(listing: Listing) { _selectedListing.value = listing }
    fun clearSelectedListing()          { _selectedListing.value = null }

    fun loadListingById(id: String) {
        viewModelScope.launch {
            _selectedListing.value = repo.getListingById(id)
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    fun deactivateListing(listingId: String) {
        viewModelScope.launch {
            try {
                repo.deactivateListing(listingId)
                loadMyListings()
            } catch (e: Exception) {
                _feedState.value = ListingUiState.Error(e.message ?: "Could not remove listing")
            }
        }
    }

    // ── Create form ───────────────────────────────────────────────────────────

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

        val user = auth.currentUser ?: run {
            _formState.update { it.copy(errorMessage = "You must be logged in to post.") }
            return
        }

        _formState.update { it.copy(isSubmitting = true, errorMessage = null) }

        val newListing = Listing(
            userId            = user.uid,
            ownerName         = user.displayName ?: "Anonymous",
            ownerPhotoUrl     = user.photoUrl?.toString(),
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
            isActive          = true
        )

        viewModelScope.launch {
            try {
                repo.createListing(newListing)
                _formState.value = CreateListingFormState(submitSuccess = true)
            } catch (e: Exception) {
                _formState.update {
                    it.copy(isSubmitting = false, errorMessage = e.message ?: "Submission failed.")
                }
            }
        }
    }

    fun resetForm() { _formState.value = CreateListingFormState() }
}
