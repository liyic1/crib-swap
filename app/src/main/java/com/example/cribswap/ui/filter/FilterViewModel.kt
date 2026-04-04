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

    // Draft = what the user is editing inside the sheet
    private val _draft = MutableStateFlow(defaultState)
    val draft: StateFlow<FilterState> = _draft.asStateFlow()

    // Applied = filters actually being used by the listings screen
    private val _applied = MutableStateFlow(defaultState)
    val applied: StateFlow<FilterState> = _applied.asStateFlow()

    fun updateDraft(update: FilterState.() -> FilterState) {
        _draft.value = _draft.value.update()
    }

    fun applyFilters() {
        _applied.value = _draft.value
    }

    fun resetFilters() {
        _draft.value = defaultState
    }
}