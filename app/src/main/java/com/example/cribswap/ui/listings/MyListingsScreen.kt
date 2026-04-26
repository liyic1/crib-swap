package com.example.cribswap.ui.listings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.data.model.Listing

/**
 * Shows all listings posted by the currently signed-in user.
 * Accessible from the Profile tab.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(
    viewModel: ListingViewModel = viewModel(),
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Listing) -> Unit,
    onNavigateToEdit: (Listing) -> Unit
) {
    val feedState by viewModel.feedState.collectAsState()
    var listingToDelete by remember { mutableStateOf<Listing?>(null) }

    // When entering this screen, filter feed to current user's listings
    LaunchedEffect(Unit) { viewModel.loadMyListings() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Listings", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Post new listing")
            }
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = feedState) {

                is ListingUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ListingUiState.Error -> {
                    Text(
                        state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is ListingUiState.Success -> {
                    if (state.listings.isEmpty()) {
                        NoListingsYet(
                            modifier = Modifier.align(Alignment.Center),
                            onPost = onNavigateToCreate
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.listings, key = { it.id }) { listing ->
                                MyListingItem(
                                    listing = listing,
                                    onView   = { onNavigateToDetail(listing) },
                                    onEdit   = { onNavigateToEdit(listing) },
                                    onDelete = { listingToDelete = listing }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Confirm delete dialog ─────────────────────────────────────────────────
    listingToDelete?.let { listing ->
        AlertDialog(
            onDismissRequest = { listingToDelete = null },
            title = { Text("Remove listing?") },
            text  = { Text("\"${listing.title}\" will be hidden from the feed. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deactivateListing(listing.id)
                        listingToDelete = null
                    }
                ) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { listingToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

// ── My Listing Row ────────────────────────────────────────────────────────────

@Composable
private fun MyListingItem(
    listing: Listing,
    onView:   () -> Unit,
    onEdit:   () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$${listing.rent.toInt()}/mo · ${listing.city}, ${listing.state}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                ActiveBadge(listing.isActive)
            }

            // Actions
            Row {
                IconButton(onClick = onView) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "View",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveBadge(isActive: Boolean) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        color = if (isActive)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.errorContainer
    ) {
        Text(
            text = if (isActive) "Active" else "Inactive",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = if (isActive)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun NoListingsYet(
    modifier: Modifier = Modifier,
    onPost: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("You haven't posted any listings yet.",
            style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onPost) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Post your first listing")
        }
    }
}