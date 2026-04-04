package com.example.cribswap.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.ui.filter.FilterBottomSheet
import com.example.cribswap.ui.filter.FilterViewModel
import com.example.cribswap.ui.theme.CribSwapBlue
import com.example.cribswap.ui.theme.DividerColor
import com.example.cribswap.ui.theme.SurfaceLight
import com.example.cribswap.ui.theme.TextPrimary
import com.example.cribswap.ui.theme.TextSecondary

// Placeholder data — teammate replaces this with their real model
private data class ListingPlaceholder(
    val title: String,
    val location: String,
    val price: String,
    val beds: String,
    val baths: String
)

private val placeholderListings = listOf(
    ListingPlaceholder("Modern Downtown Apartment", "123 Main St", "$1,200/mo", "2 bd", "1 ba"),
    ListingPlaceholder("Cozy Studio Near Campus",  "456 College Ave", "$750/mo", "Studio", "1 ba"),
    ListingPlaceholder("Spacious 3BR House",        "789 Oak Lane",   "$2,100/mo", "3 bd", "2 ba"),
    ListingPlaceholder("Luxury Condo with Parking", "321 River Rd",   "$1,800/mo", "2 bd", "2 ba"),
    ListingPlaceholder("Bright 1BR Apartment",      "654 Elm St",     "$950/mo",  "1 bd", "1 ba"),
    ListingPlaceholder("Shared House Room",         "987 Maple Ave",  "$600/mo",  "1 bd", "1 ba"),
)

@Composable
fun Home(
    modifier: Modifier = Modifier,
    filterViewModel: FilterViewModel = viewModel()
) {
    var showFilter by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .background(SurfaceLight)
        ) {
            // Top bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(CribSwapBlue)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Listings",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = { showFilter = true },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Search, contentDescription = "Filter",
                        tint = Color.White, modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Filter", fontSize = 14.sp)
                }
            }

            // Placeholder listing cards
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(placeholderListings.size) { i ->
                    PlaceholderListingCard(placeholderListings[i])
                }
            }
        }

        // Filter sheet overlays everything in this Box
        if (showFilter) {
            FilterBottomSheet(
                onDismiss = { showFilter = false },
                viewModel = filterViewModel
            )
        }
    }
}

@Composable
private fun PlaceholderListingCard(listing: ListingPlaceholder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Image placeholder
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DividerColor),
                contentAlignment = Alignment.Center
            ) {
                Text("Photo Coming Soon", color = TextSecondary, fontSize = 13.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(listing.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(listing.location, fontSize = 13.sp, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(listing.price, fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, color = CribSwapBlue)
                Text("${listing.beds}  ·  ${listing.baths}",
                    fontSize = 13.sp, color = TextSecondary)
            }
        }
    }
}