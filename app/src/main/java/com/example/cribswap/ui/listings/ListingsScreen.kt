package com.example.cribswap.ui.listings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bed
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

data class FilterSheetValues(
    val maxRent: String = "",
    val bedrooms: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(
    viewModel: ListingViewModel = viewModel(),
    onNavigateToDetail: (Listing) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val feedState by viewModel.feedState.collectAsState()
    val maxRentFilter by viewModel.maxRentFilter.collectAsState()
    val bedroomsFilter by viewModel.bedroomsFilter.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var filterValues by remember { mutableStateOf(FilterSheetValues()) }

    val filtersActive = maxRentFilter != null || bedroomsFilter != null

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
            when (val state = feedState) {
                is ListingUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ListingUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadListings() }) { Text("Retry") }
                    }
                }

                is ListingUiState.Success -> {
                    if (state.listings.isEmpty()) {
                        EmptyListingsMessage(
                            modifier = Modifier.align(Alignment.Center),
                            filtersActive = filtersActive,
                            onClearFilters = { viewModel.clearFilters() }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.listings, key = { it.id }) { listing ->
                                ListingCard(
                                    listing = listing,
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

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            FilterSheetContent(
                values = filterValues,
                onValuesChange = { filterValues = it },
                onApply = {
                    viewModel.applyFilters(
                        maxRent = filterValues.maxRent.toDoubleOrNull(),
                        bedrooms = filterValues.bedrooms.toIntOrNull()
                    )
                    showFilterSheet = false
                },
                onClear = {
                    filterValues = FilterSheetValues()
                    viewModel.clearFilters()
                    showFilterSheet = false
                }
            )
        }
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

@Composable
private fun FilterSheetContent(
    values: FilterSheetValues,
    onValuesChange: (FilterSheetValues) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Filter Listings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = values.maxRent,
            onValueChange = { onValuesChange(values.copy(maxRent = it)) },
            label = { Text("Max Rent ($/mo)") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
            singleLine = true
        )

        OutlinedTextField(
            value = values.bedrooms,
            onValueChange = { onValuesChange(values.copy(bedrooms = it)) },
            label = { Text("Bedrooms") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Bed, contentDescription = null) },
            singleLine = true
        )

        Spacer(Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Clear") }
            Button(onClick = onApply, modifier = Modifier.weight(1f)) { Text("Apply") }
        }
    }
}