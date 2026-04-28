package com.example.cribswap.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.ui.listings.ListingCard
import com.example.cribswap.ui.listings.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Saved(
    modifier: Modifier = Modifier,
    listingViewModel: ListingViewModel = viewModel()
) {
    val savedIds by listingViewModel.savedListingIds.collectAsState()
    val savedListings = listingViewModel.getSavedListings()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        if (savedListings.isEmpty()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "No saved listings yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Heart a listing to save it here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyColumn(
                modifier = modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedListings, key = { it.id }) { listing ->
                    ListingCard(
                        listing = listing,
                        isSaved = listing.id in savedIds,
                        onSaveToggle = { listingViewModel.toggleSaved(listing) },
                        onClick = { }
                    )
                }
            }
        }
    }
}