package com.example.cribswap.ui.listings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cribswap.data.model.Listing
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingId: String? = null,
    viewModel: ListingViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onMessageOwner: (String) -> Unit  // pass owner userId
) {
    val listing by viewModel.selectedListing.collectAsState()

    // If we only have an ID (e.g., deep link), load it
    LaunchedEffect(listingId) {
        if (listingId != null && listing?.id != listingId) {
            viewModel.loadListingById(listingId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listing Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (listing == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            ListingDetailContent(
                listing = listing!!,
                modifier = Modifier.padding(padding),
                onMessageOwner = { onMessageOwner(listing!!.userId) }
            )
        }
    }
}

@Composable
private fun ListingDetailContent(
    listing: Listing,
    modifier: Modifier = Modifier,
    onMessageOwner: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.US) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Photos ────────────────────────────────────────────────────────────
        if (listing.photoUrls.isNotEmpty()) {
            AsyncImage(
                model = listing.photoUrls.first(),
                contentDescription = "Listing photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(260.dp)
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            // ── Price & Title ─────────────────────────────────────────────────
            Text(
                text = listing.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${currencyFormat.format(listing.rent)}/month",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            // ── Location ──────────────────────────────────────────────────────
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${listing.address}, ${listing.city}, ${listing.state} ${listing.zipCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // ── Key Details Grid ──────────────────────────────────────────────
            Text("Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                DetailItem(Icons.Default.Bed, "Bedrooms", "${listing.bedrooms}", Modifier.weight(1f))
                DetailItem(Icons.Default.Bathtub, "Bathrooms", "${listing.bathrooms}", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                if (listing.squareFeet != null) {
                    DetailItem(Icons.Default.SquareFoot, "Sq Ft", "${listing.squareFeet}", Modifier.weight(1f))
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.weight(1f))
            }

            // ── Amenity Chips ─────────────────────────────────────────────────
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (listing.isFurnished)      AmenityChip("Furnished")
                if (listing.petsAllowed)      AmenityChip("Pets Allowed")
                if (listing.utilitiesIncluded) AmenityChip("Utilities Included")
            }

            // ── Lease Window ──────────────────────────────────────────────────
            if (listing.leaseStart != null || listing.leaseEnd != null) {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Lease Dates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listing.leaseStart?.let {
                        DetailItem(Icons.Default.CalendarToday, "Start", dateFormat.format(it.toDate()), Modifier.weight(1f))
                    }
                    listing.leaseEnd?.let {
                        DetailItem(Icons.Default.EventAvailable, "End", dateFormat.format(it.toDate()), Modifier.weight(1f))
                    }
                }
            }

            // ── Description ───────────────────────────────────────────────────
            if (listing.description.isNotBlank()) {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                Text("About this place", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(listing.description, style = MaterialTheme.typography.bodyMedium)
            }

            // ── Owner Card ────────────────────────────────────────────────────
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Text("Posted by", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (listing.ownerPhotoUrl != null) {
                    AsyncImage(
                        model = listing.ownerPhotoUrl,
                        contentDescription = "Owner photo",
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    listing.ownerName.ifBlank { "Anonymous" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            // ── CTA ───────────────────────────────────────────────────────────
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onMessageOwner,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Message, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Message Owner")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AmenityChip(label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}