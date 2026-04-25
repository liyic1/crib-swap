package com.example.cribswap.ui.filter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FilterViewModel : ViewModel() {

    private val currentYear: Int
        get() = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

    private val defaultState: FilterState
        get() = FilterState(leaseStartYear = currentYear, leaseEndYear = currentYear)

    // draft: user is editing filter preferences
    // applies: copy of draft whe user clicks "apply changes"
    private val _draft = MutableStateFlow(defaultState)
    val draft: StateFlow<FilterState> = _draft.asStateFlow()

    private val _applied = MutableStateFlow(defaultState)
    val applied: StateFlow<FilterState> = _applied.asStateFlow()

    fun updateDraft(update: FilterState.() -> FilterState) {
        _draft.value = _draft.value.update()
    }

    // copy draft to applied
    fun applyFilters() {
        _applied.value = _draft.value
    }

    // clears draft (applied is unchanged)
    fun resetFilters() {
        _draft.value = defaultState
    }

    fun applyPreferences(state: FilterState) {
        _draft.value = state
        _applied.value = state
    }
}
