package com.example.cribswap.ui.filter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class PreferencesViewModel : ViewModel() {

    private val currentYear: Int
        get() = Calendar.getInstance().get(Calendar.YEAR)

    private val _onboardingComplete = MutableStateFlow(false)
    val onboardingComplete: StateFlow<Boolean> = _onboardingComplete.asStateFlow()

    private val _prefs = MutableStateFlow(PreferencesFormState(
        leaseStartYear = currentYear,
        leaseEndYear = currentYear
    ))
    val prefs: StateFlow<PreferencesFormState> = _prefs.asStateFlow()

    fun updatePrefs(update: PreferencesFormState.() -> PreferencesFormState) {
        _prefs.value = _prefs.value.update()
    }

    // connects filter and preferences, sets both draft and applied
    fun completeOnboarding(filterViewModel: FilterViewModel) {
        val p = _prefs.value
        filterViewModel.applyPreferences(
            FilterState(
                priceMin        = p.priceMin,
                priceMax        = p.priceMax,
                locationQuery   = p.locationQuery,
                distanceMiles   = p.distanceMiles,
                leaseStartMonth = p.leaseStartMonth,
                leaseStartYear  = p.leaseStartYear,
                leaseEndMonth   = p.leaseEndMonth,
                leaseEndYear    = p.leaseEndYear
            )
        )
        _onboardingComplete.value = true
    }
}

data class PreferencesFormState(
    val priceMin: Float = 0f,
    val priceMax: Float = 3000f,
    val locationQuery: String = "",
    val distanceMiles: Float = 5f,
    val leaseStartMonth: String? = null,
    val leaseStartYear: Int = 0,
    val leaseEndMonth: String? = null,
    val leaseEndYear: Int = 0
)