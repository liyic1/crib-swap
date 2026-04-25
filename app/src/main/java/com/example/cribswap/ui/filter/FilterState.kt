package com.example.cribswap.ui.filter

import com.google.firebase.Timestamp
import java.util.Calendar

/**
 * Filter criteria for sublease listings.
 *
 * IMPORTANT: Some UI fields (laundry, parking, roommates, buildingTypes)
 * are NOT in the Listing data model and will be ignored until backend schema updates.
 */
data class FilterState(
    // ── Supported Filters (match Listing model) ──────────────────────────────
    val priceMin: Float = 0f,
    val priceMax: Float = 3000f,
    val locationQuery: String = "",
    val distanceMiles: Float = 5f,

    // Bedrooms: stored as Int in Firestore (0=Studio, 1-4=bedrooms, 5=4+)
    val bedrooms: List<Int> = emptyList(),

    // Bathrooms: stored as Double in Firestore (0.5, 1.0, 1.5, 2.0, 2.5, 3.0+)
    val bathrooms: List<Double> = emptyList(),

    val leaseStartMonth: String? = null,
    val leaseStartYear: Int = 0,
    val leaseEndMonth: String? = null,
    val leaseEndYear: Int = 0,

    val furnished: Boolean = false,
    val photosRequired: Boolean = false,

    // ── Unsupported Filters (not in Listing model yet) ──────────────────────
    // These are kept for UI compatibility but won't filter results
    val inUnitLaundry: Boolean = false,      // TODO: Add to Listing model
    val parking: Boolean = false,            // TODO: Add to Listing model
    val roommates: List<Int> = emptyList(),  // TODO: Add to Listing model
    val buildingTypes: List<String> = emptyList()  // TODO: Add to Listing model
) {
    /**
     * Convert month/year selections to Firebase Timestamps for querying.
     * Returns null if no month is selected.
     */
    fun getLeaseStartTimestamp(): Timestamp? {
        if (leaseStartMonth == null) return null
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, leaseStartYear)
            set(Calendar.MONTH, monthNameToInt(leaseStartMonth))
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return Timestamp(calendar.time)
    }

    fun getLeaseEndTimestamp(): Timestamp? {
        if (leaseEndMonth == null) return null
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, leaseEndYear)
            set(Calendar.MONTH, monthNameToInt(leaseEndMonth))
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return Timestamp(calendar.time)
    }

    private fun monthNameToInt(monthName: String): Int = when (monthName) {
        "Jan" -> Calendar.JANUARY
        "Feb" -> Calendar.FEBRUARY
        "Mar" -> Calendar.MARCH
        "Apr" -> Calendar.APRIL
        "May" -> Calendar.MAY
        "Jun" -> Calendar.JUNE
        "Jul" -> Calendar.JULY
        "Aug" -> Calendar.AUGUST
        "Sep" -> Calendar.SEPTEMBER
        "Oct" -> Calendar.OCTOBER
        "Nov" -> Calendar.NOVEMBER
        "Dec" -> Calendar.DECEMBER
        else -> Calendar.JANUARY
    }
}

/**
 * Helper to convert UI bedroom strings to Firestore Int values.
 */
object BedroomMapper {
    fun stringToInt(bedroom: String): Int = when (bedroom) {
        "Studio" -> 0
        "4+" -> 5  // Store 4+ as 5 to allow whereIn queries
        else -> bedroom.toIntOrNull() ?: 1
    }

    fun intToString(bedrooms: Int): String = when (bedrooms) {
        0 -> "Studio"
        5 -> "4+"
        else -> bedrooms.toString()
    }
}

/**
 * Helper to convert UI bathroom strings to Firestore Double values.
 */
object BathroomMapper {
    fun stringToDouble(bathroom: String): Double = when (bathroom) {
        "3+" -> 3.5  // Store 3+ as 3.5 to allow filtering >= 3.0
        else -> bathroom.toDoubleOrNull() ?: 1.0
    }

    fun doubleToString(bathrooms: Double): String = when {
        bathrooms >= 3.5 -> "3+"
        else -> bathrooms.toString()
    }
}
