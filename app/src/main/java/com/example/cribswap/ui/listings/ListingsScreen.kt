package com.example.cribswap.ui.listings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.data.model.Listing
import com.example.cribswap.ui.filter.FilterBottomSheet
import com.example.cribswap.ui.filter.FilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(
    viewModel: ListingViewModel = viewModel(),
    filterViewModel: FilterViewModel = viewModel(),  // 🔥 NEW: Accept FilterViewModel
    onNavigateToDetail: (Listing) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    // 🔥 NEW: Get filtered listings from FilterViewModel
    val filteredListingsResult by filterViewModel.filteredListings.collectAsState()
    val appliedFilters by filterViewModel.applied.collectAsState()
    val savedIds by viewModel.savedListingIds.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    // 🔥 NEW: Determine if any filters are active
    val filtersActive = appliedFilters.priceMin > 0f ||
            appliedFilters.priceMax < 3000f ||
            appliedFilters.locationQuery.isNotBlank() ||
            appliedFilters.bedrooms.isNotEmpty() ||
            appliedFilters.bathrooms.isNotEmpty() ||
            appliedFilters.leaseStartMonth != null ||
            appliedFilters.leaseEndMonth != null ||
            appliedFilters.furnished ||
            appliedFilters.photosRequired

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CribSwap", fontWeight = FontWeight.Bold) },
                actions = {
                    BadgedBox(
                        badge = { if (filtersActive) Badge() }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Post a listing")
            }
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // 🔥 NEW: Handle filtered listings result
            when {
                filteredListingsResult.isFailure -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            filteredListingsResult.exceptionOrNull()?.message ?: "Error loading listings",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { filterViewModel.applyFilters() }) {
                            Text("Retry")
                        }
                    }
                }

                filteredListingsResult.isSuccess -> {
                    val listings = filteredListingsResult.getOrNull() ?: emptyList()

                    if (listings.isEmpty()) {
                        EmptyListingsMessage(
                            modifier = Modifier.align(Alignment.Center),
                            filtersActive = filtersActive,
                            onClearFilters = { filterViewModel.resetFilters() }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listings, key = { it.id }) { listing ->
                                ListingCard(
                                    listing = listing,
                                    isSaved = listing.id in savedIds,
                                    onSaveToggle = { viewModel.toggleSaved(listing) },
                                    onClick = {
                                        viewModel.selectListing(listing)
                                        onNavigateToDetail(listing)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 🔥 NEW: Use your FilterBottomSheet instead of the simple one
    if (showFilterSheet) {
        FilterBottomSheet(
            onDismiss = { showFilterSheet = false },
            viewModel = filterViewModel
        )
    }
}

@Composable
private fun EmptyListingsMessage(
    filtersActive: Boolean,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            if (filtersActive) "No listings match your filters."
            else "No listings yet. Be the first to post!",
            style = MaterialTheme.typography.bodyLarge
        )
        if (filtersActive) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onClearFilters) { Text("Clear Filters") }
        }
    }
}
