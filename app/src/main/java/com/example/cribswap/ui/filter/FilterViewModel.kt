package com.example.cribswap.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cribswap.data.model.Listing
import com.example.cribswap.data.repo.SubleaseRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class FilterViewModel(
    private val repository: SubleaseRepository,
    private val userLatitude: Double? = null,
    private val userLongitude: Double? = null
) : ViewModel() {

    constructor() : this(
        repository = SubleaseRepository(FirebaseFirestore.getInstance()),
        userLatitude = null,
        userLongitude = null
    )

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
        .flatMapLatest { filters ->
            repository.getFilteredListings(
                filters = filters,
                userLatitude = userLatitude,
                userLongitude = userLongitude
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.success(emptyList())
        )

    fun updateDraft(update: FilterState.() -> FilterState) {
        _draft.value = _draft.value.update()
    }

    fun applyFilters() {
        _applied.value = _draft.value
    }

    fun resetFilters() {
        _draft.value = defaultState
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

class PreferencesViewModelFactory(
    private val filterViewModel: FilterViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreferencesViewModel(filterViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}