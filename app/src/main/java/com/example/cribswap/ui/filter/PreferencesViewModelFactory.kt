package com.example.cribswap.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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