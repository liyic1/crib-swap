package com.example.cribswap.ui.listings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    viewModel: ListingViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val form by viewModel.formState.collectAsState()

    // Navigate away when submission succeeds
    LaunchedEffect(form.submitSuccess) {
        if (form.submitSuccess) {
            viewModel.resetForm()
            onSubmitSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post a Listing", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Error banner ──────────────────────────────────────────────────
            form.errorMessage?.let { error ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(Modifier.width(8.dp))
                        Text(error, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            // ─── Section: Basic Info ──────────────────────────────────────────
            SectionHeader("Basic Info")

            OutlinedTextField(
                value = form.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Listing Title *") },
                placeholder = { Text("e.g. Cozy 2BR near campus") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = form.rent,
                onValueChange = viewModel::onRentChange,
                label = { Text("Monthly Rent ($) *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = form.bedrooms,
                    onValueChange = viewModel::onBedroomsChange,
                    label = { Text("Bedrooms") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Bed, contentDescription = null) }
                )
                OutlinedTextField(
                    value = form.bathrooms,
                    onValueChange = viewModel::onBathroomsChange,
                    label = { Text("Bathrooms") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Bathtub, contentDescription = null) }
                )
            }

            OutlinedTextField(
                value = form.squareFeet,
                onValueChange = viewModel::onSqFtChange,
                label = { Text("Square Feet (optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // ─── Section: Location ────────────────────────────────────────────
            SectionHeader("Location")

            OutlinedTextField(
                value = form.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text("Street Address *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = form.city,
                    onValueChange = viewModel::onCityChange,
                    label = { Text("City *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.state,
                    onValueChange = viewModel::onStateChange,
                    label = { Text("State *") },
                    modifier = Modifier.weight(0.6f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = form.zipCode,
                onValueChange = viewModel::onZipChange,
                label = { Text("ZIP Code *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // ─── Section: Amenities ───────────────────────────────────────────
            SectionHeader("Amenities")

            AmenityToggleRow(
                icon = Icons.Default.Chair,
                label = "Furnished",
                checked = form.isFurnished,
                onToggle = viewModel::onFurnishedToggle
            )
            AmenityToggleRow(
                icon = Icons.Default.Pets,
                label = "Pets Allowed",
                checked = form.petsAllowed,
                onToggle = viewModel::onPetsToggle
            )
            AmenityToggleRow(
                icon = Icons.Default.Bolt,
                label = "Utilities Included",
                checked = form.utilitiesIncluded,
                onToggle = viewModel::onUtilitiesToggle
            )

            // ─── Section: Description ─────────────────────────────────────────
            SectionHeader("Description")

            OutlinedTextField(
                value = form.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Tell renters about this place") },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                maxLines = 6
            )

            // ─── Submit ───────────────────────────────────────────────────────
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::submitListing,
                enabled = !form.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (form.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Posting…")
                } else {
                    Icon(Icons.Default.Publish, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Post Listing")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun AmenityToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(10.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}