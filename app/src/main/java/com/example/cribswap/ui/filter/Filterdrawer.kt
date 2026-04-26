package com.example.cribswap.ui.filter

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    viewModel: FilterViewModel = viewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val draft by viewModel.draft.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceLight,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null
    ) {
        Column(Modifier.fillMaxWidth()) {
            // ── Header ───────────────────────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(CribSwapBlue)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Box(
                    Modifier
                        .width(40.dp).height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.4f))
                        .align(Alignment.TopCenter)
                )
                Text(
                    "Filter Listings",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                )
            }

            // ── Filter Content ───────────────────────────────────────────────
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Price Range
                FilterSection("Price Range") {
                    Box(Modifier.fillMaxWidth().height(22.dp)) {
                        Text(
                            "$${draft.priceMin.toInt()} – $${draft.priceMax.toInt()} / mo",
                            fontSize = 13.sp,
                            color = CribSwapBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    RangeSlider(
                        value = draft.priceMin..draft.priceMax,
                        onValueChange = {
                            viewModel.updateDraft {
                                copy(priceMin = it.start, priceMax = it.endInclusive)
                            }
                        },
                        valueRange = 0f..5000f,
                        colors = SliderDefaults.colors(
                            thumbColor = CribSwapBlue,
                            activeTrackColor = CribSwapBlue,
                            inactiveTrackColor = DividerColor
                        )
                    )
                    MinMaxLabel("$0", "$5000")
                }

                FilterDivider()

                // Location
                FilterSection("Location") {
                    OutlinedTextField(
                        value = draft.locationQuery,
                        onValueChange = {
                            viewModel.updateDraft { copy(locationQuery = it) }
                        },
                        placeholder = { Text("Address or zip code", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.LocationOn,
                                null,
                                tint = CribSwapBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CribSwapBlue,
                            unfocusedBorderColor = DividerColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth().height(22.dp)) {
                        Text(
                            "Within ${draft.distanceMiles.toInt()} mile${if (draft.distanceMiles > 1f) "s" else ""}",
                            fontSize = 13.sp,
                            color = CribSwapBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = draft.distanceMiles,
                        onValueChange = {
                            viewModel.updateDraft { copy(distanceMiles = it) }
                        },
                        valueRange = 1f..50f,
                        steps = 48,
                        colors = SliderDefaults.colors(
                            thumbColor = CribSwapBlue,
                            activeTrackColor = CribSwapBlue,
                            inactiveTrackColor = DividerColor
                        )
                    )
                    MinMaxLabel("1 mile", "50+ miles")
                }

                FilterDivider()

                // Bedrooms - UI shows strings, ViewModel converts to Int
                FilterSection("Bedrooms") {
                    val bedroomOptions = listOf("Studio", "1", "2", "3", "4+")
                    val selectedBedrooms = draft.bedrooms.map {
                        BedroomMapper.intToString(it)
                    }

                    SelectionButtonRow(
                        options = bedroomOptions,
                        selected = selectedBedrooms,
                        onToggle = { viewModel.toggleBedroom(it) }
                    )
                }

                FilterDivider()

                // Bathrooms - UI shows strings, ViewModel converts to Double
                FilterSection("Bathrooms") {
                    val bathroomOptions = listOf("0.5", "1", "1.5", "2", "2.5", "3+")
                    val selectedBathrooms = draft.bathrooms.map {
                        BathroomMapper.doubleToString(it)
                    }

                    SelectionButtonRow(
                        options = bathroomOptions,
                        selected = selectedBathrooms,
                        onToggle = { viewModel.toggleBathroom(it) }
                    )
                }

                FilterDivider()

                // Lease Term
                FilterSection("Lease Term") {
                    Text(
                        "Start",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(6.dp))
                    MonthYearPicker(
                        selectedMonth = draft.leaseStartMonth,
                        selectedYear = draft.leaseStartYear,
                        onYearChange = {
                            viewModel.updateDraft { copy(leaseStartYear = it) }
                        },
                        onMonthSelect = {
                            viewModel.updateDraft {
                                copy(leaseStartMonth = if (leaseStartMonth == it) null else it)
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "End",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(6.dp))
                    MonthYearPicker(
                        selectedMonth = draft.leaseEndMonth,
                        selectedYear = draft.leaseEndYear,
                        onYearChange = {
                            viewModel.updateDraft { copy(leaseEndYear = it) }
                        },
                        onMonthSelect = {
                            viewModel.updateDraft {
                                copy(leaseEndMonth = if (leaseEndMonth == it) null else it)
                            }
                        }
                    )
                }

                FilterDivider()

                // Amenities
                FilterSection("Amenities") {
                    ToggleRow("Furnished", draft.furnished) {
                        viewModel.updateDraft { copy(furnished = it) }
                    }

                    ToggleRow("Photos Required", draft.photosRequired) {
                        viewModel.updateDraft { copy(photosRequired = it) }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            // ── Footer Buttons ───────────────────────────────────────────────
            HorizontalDivider(color = DividerColor)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetFilters() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, CribSwapBlue),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CribSwapBlue
                    )
                ) {
                    Text("Reset", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        viewModel.applyFilters()
                        onDismiss()
                    },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CribSwapBlue
                    )
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Reusable Components ──────────────────────────────────────────────────────

@Composable
private fun FilterSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(vertical = 10.dp)) {
        Text(
            title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun FilterDivider() = HorizontalDivider(color = DividerColor, thickness = 1.dp)

@Composable
private fun MinMaxLabel(min: String, max: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(min, fontSize = 11.sp, color = TextSecondary)
        Text(max, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun SelectionButtonRow(
    options: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = selected.contains(option)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) CribSwapBlue else CribSwapBlueLight)
                    .border(
                        1.dp,
                        if (isSelected) CribSwapBlue else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onToggle(option) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    option,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CribSwapBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = DividerColor
            )
        )
    }
}