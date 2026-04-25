package com.example.cribswap.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cribswap.data.model.Listing
import com.example.cribswap.data.repo.SubleaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for the filter bottom sheet.
 *
 * PATTERN:
 * - draft: User's current edits (not yet applied)
 * - applied: Active filters affecting the listings shown
 * - When user clicks "Apply", draft -> applied
 * - When user clicks "Reset", draft resets to defaults (applied unchanged)
 */
class FilterViewModel(
    private val repository: SubleaseRepository,
    private val userLatitude: Double? = null,  // Inject from location service
    private val userLongitude: Double? = null
) : ViewModel() {

    private val currentYear: Int
        get() = Calendar.getInstance().get(Calendar.YEAR)

    private val defaultState: FilterState
        get() = FilterState(
            leaseStartYear = currentYear,
            leaseEndYear = currentYear
        )

    // ── UI State ─────────────────────────────────────────────────────────────

    // Draft: what user is currently editing
    private val _draft = MutableStateFlow(defaultState)
    val draft: StateFlow<FilterState> = _draft.asStateFlow()

    // Applied: what's actually filtering the listings
    private val _applied = MutableStateFlow(defaultState)
    val applied: StateFlow<FilterState> = _applied.asStateFlow()

    // ── Filtered Listings ────────────────────────────────────────────────────

    /**
     * Live stream of filtered listings.
     * Updates whenever applied filters change OR Firestore data changes.
     */
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

    // ── Actions ──────────────────────────────────────────────────────────────

    fun updateDraft(update: FilterState.() -> FilterState) {
        _draft.value = _draft.value.update()
    }

    /**
     * User clicked "Apply Filters" - copy draft to applied.
     * This triggers filteredListings to re-query with new criteria.
     */
    fun applyFilters() {
        _applied.value = _draft.value
    }

    /**
     * User clicked "Reset" - clear draft back to defaults.
     * Applied filters remain unchanged (so listings don't change).
     */
    fun resetFilters() {
        _draft.value = defaultState
    }

    /**
     * Called from PreferencesScreen when user completes onboarding.
     * Sets both draft AND applied so filters are immediately active.
     */
    fun applyPreferences(state: FilterState) {
        _draft.value = state
        _applied.value = state
    }

    // ── UI Helper Methods ────────────────────────────────────────────────────

    /**
     * Convert UI bedroom string selections to Int list for FilterState.
     * Called when user toggles bedroom buttons.
     */
    fun toggleBedroom(bedroomString: String) {
        val bedroomInt = BedroomMapper.stringToInt(bedroomString)
        updateDraft {
            copy(bedrooms = bedrooms.toggle(bedroomInt))
        }
    }

    /**
     * Convert UI bathroom string selections to Double list for FilterState.
     */
    fun toggleBathroom(bathroomString: String) {
        val bathroomDouble = BathroomMapper.stringToDouble(bathroomString)
        updateDraft {
            copy(bathrooms = bathrooms.toggle(bathroomDouble))
        }
    }

    /**
     * Toggle for roommates (currently not in Listing model but kept for UI).
     */
    fun toggleRoommate(roommateString: String) {
        val roommateInt = when (roommateString) {
            "4+" -> 5
            else -> roommateString.toIntOrNull() ?: 0
        }
        updateDraft {
            copy(roommates = roommates.toggle(roommateInt))
        }
    }

    /**
     * Toggle building types (currently not in Listing model but kept for UI).
     */
    fun toggleBuildingType(type: String) {
        updateDraft {
            copy(buildingTypes = buildingTypes.toggle(type))
        }
    }
}

// ── Extension Helper ─────────────────────────────────────────────────────────

private fun <T> List<T>.toggle(item: T): List<T> =
    if (contains(item)) this - item else this + item