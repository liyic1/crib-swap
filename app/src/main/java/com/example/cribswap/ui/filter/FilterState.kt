package com.example.cribswap.ui.filter

data class FilterState(
    val priceMin: Float = 0f,
    val priceMax: Float = 3000f,
    val locationQuery: String = "",
    val distanceMiles: Float = 5f,
    val bedrooms: List<String> = emptyList(),
    val bathrooms: List<String> = emptyList(),
    val roommates: List<String> = emptyList(),
    val leaseStartMonth: String? = null,
    val leaseStartYear: Int = 0,
    val leaseEndMonth: String? = null,
    val leaseEndYear: Int = 0,
    val furnished: Boolean = false,
    val inUnitLaundry: Boolean = false,
    val parking: Boolean = false,
    val photosRequired: Boolean = false,
    val buildingTypes: List<String> = emptyList()
)
