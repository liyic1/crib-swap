package com.example.cribswap

import com.example.cribswap.ui.theme.Filter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.cribswap.ui.theme.CribSwapTheme

// State Management Imports
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CribSwapTheme {
                val filters = listOf(
                    "Distance",
                    "Price",
                    "Lease Term (Start Month - End Month)",
                    "Bedrooms",
                    "Bathrooms",
                    "Amenities Included",
                    "In-Unit Laundry"
                )
                var selectedFilters by remember { mutableStateOf(setOf("All")) }
                var isFilterVisible by remember { mutableStateOf(false) }
                Column {
                    Button(
                        onClick = { isFilterVisible = !isFilterVisible },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                    AnimatedVisibility(visible = isFilterVisible) {
                        Filter(
                            filters = filters,
                            selectedFilters = selectedFilters,
                            onFilterSelected = { filter ->
                                selectedFilters = if (filter in selectedFilters) {
                                    selectedFilters - filter
                                } else {
                                    selectedFilters + filter
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}