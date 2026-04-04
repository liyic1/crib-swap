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
import com.example.cribswap.ui.theme.CribSwapBlue
import com.example.cribswap.ui.theme.CribSwapBlueLight
import java.util.Calendar

// CribSwap Colors
private val Accent         = CribSwapBlue
private val SurfaceLight   = Color(0xFFF5F9FB)
private val DividerColor   = Color(0xFFD6E6EE)
private val TextPrimary    = Color(0xFF0F2A38)
private val TextSecondary  = Color(0xFF5A7A8A)
private val ButtonSelected   = CribSwapBlue
private val ButtonUnselected = CribSwapBlueLight


private fun <T> MutableList<T>.toggle(item: T) =
    if (contains(item)) remove(item) else add(item)
private val currentYear: Int
    get() = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

@OptIn(ExperimentalMaterial3Api::class) // Need for using ModalBottomSheet
@Composable
fun FilterBottomSheet(onDismiss: () -> Unit) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Filter Vars and Vals

    var priceMin by remember { mutableFloatStateOf(0f) }
    var priceMax by remember { mutableFloatStateOf(3000f) }
    var locationQuery by remember { mutableStateOf("") }
    var distanceMiles by remember { mutableFloatStateOf(5f) }
    val bedrooms  = remember { mutableStateListOf<String>() }
    val bathrooms = remember { mutableStateListOf<String>() }
    val roommates = remember { mutableStateListOf<String>() }
    var leaseStartMonth by remember { mutableStateOf<String?>(null) }
    var leaseStartYear  by remember { mutableIntStateOf(currentYear) }
    var leaseEndMonth   by remember { mutableStateOf<String?>(null) }
    var leaseEndYear    by remember { mutableIntStateOf(currentYear) }
    var furnished      by remember { mutableStateOf(false) }
    var inUnitLaundry  by remember { mutableStateOf(false) }
    var parking        by remember { mutableStateOf(false) }
    var photosRequired by remember { mutableStateOf(false) }
    val buildingTypes  = remember { mutableStateListOf<String>() }

    // When user wants to clear filters
    fun resetAll() {
        priceMin = 0f
        priceMax = 3000f
        locationQuery = ""
        distanceMiles = 5f
        bedrooms.clear()
        bathrooms.clear()
        roommates.clear()
        leaseStartMonth = null
        leaseStartYear = currentYear
        leaseEndMonth = null
        leaseEndYear = currentYear
        furnished = false
        inUnitLaundry = false
        parking = false
        photosRequired = false
        buildingTypes.clear()
    }

    // Bottom sliding filter selection
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceLight,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            // Header
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Accent)
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
                    color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                )
            }

            // Filters (scrollable)
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Price
                FilterSection("Price Range") {
                    Box(Modifier.fillMaxWidth().height(22.dp)) {
                        Text("$${priceMin.toInt()} – $${priceMax.toInt()} / mo",
                            fontSize = 13.sp, color = Accent, fontWeight = FontWeight.SemiBold)
                    }
                    RangeSlider(
                        value = priceMin..priceMax,
                        onValueChange = { priceMin = it.start; priceMax = it.endInclusive },
                        valueRange = 0f..5000f,
                        colors = SliderDefaults.colors(thumbColor = Accent,
                            activeTrackColor = Accent, inactiveTrackColor = DividerColor)
                    )
                    MinMaxLabel("$0","$5000" )
                }

                FilterDivider()

                // Location
                FilterSection("Location") {
                    OutlinedTextField(
                        value = locationQuery, onValueChange = { locationQuery = it },
                        placeholder = { Text("Address or zip code", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null,
                            tint = Accent, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent, unfocusedBorderColor = DividerColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth().height(22.dp)) {
                        Text("Within ${distanceMiles.toInt()} mile${if (distanceMiles > 1f) "s" else ""}",
                            fontSize = 13.sp, color = Accent, fontWeight = FontWeight.SemiBold)
                    }
                    Slider(
                        value = distanceMiles, onValueChange = { distanceMiles = it },
                        valueRange = 1f..50f, steps = 48,
                        colors = SliderDefaults.colors(thumbColor = Accent,
                            activeTrackColor = Accent, inactiveTrackColor = DividerColor)
                    )
                    MinMaxLabel("1 miles", "50+ miles")
                }

                FilterDivider()

                // Bedrooms
                FilterSection("Bedrooms") {
                    SelectionButtonRow(listOf("Studio", "1", "2", "3", "4+"), bedrooms) { bedrooms.toggle(it) }
                }

                FilterDivider()

                // Bathrooms
                FilterSection("Bathrooms") {
                    SelectionButtonRow(listOf("0.5", "1", "1.5", "2", "2.5", "3+"), bathrooms) { bathrooms.toggle(it) }
                }

                FilterDivider()

                // Roommates
                FilterSection("Roommates") {
                    SelectionButtonRow(listOf("0", "1", "2", "3", "4+"), roommates) { roommates.toggle(it) }
                }

                FilterDivider()

                // Lease Term
                FilterSection("Lease Term") {
                    Text("Start", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    MonthYearPicker(leaseStartMonth, leaseStartYear,
                        onYearChange = { leaseStartYear = it },
                        onMonthSelect = { leaseStartMonth = if (leaseStartMonth == it) null else it })
                    Spacer(Modifier.height(14.dp))
                    Text("End", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    MonthYearPicker(leaseEndMonth, leaseEndYear,
                        onYearChange = { leaseEndYear = it },
                        onMonthSelect = { leaseEndMonth = if (leaseEndMonth == it) null else it })
                }

                FilterDivider()

                // Building Type
                FilterSection("Building Type") {
                    listOf("Apartment", "Condo", "House").forEach { type ->
                        Row(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { buildingTypes.toggle(type) }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = buildingTypes.contains(type),
                                onCheckedChange = { buildingTypes.toggle(type) },
                                colors = CheckboxDefaults.colors(checkedColor = Accent)
                            )
                            Text(type, fontSize = 14.sp, color = TextPrimary)
                        }
                    }
                }

                FilterDivider()

                // Amenities
                FilterSection("Amenities") {
                    ToggleRow("Furnished",        furnished)      { furnished = it }
                    ToggleRow("In-Unit Laundry",  inUnitLaundry)  { inUnitLaundry = it }
                    ToggleRow("Parking Included", parking)        { parking = it }
                    ToggleRow("Photos Required",  photosRequired) { photosRequired = it }
                }

                Spacer(Modifier.height(8.dp))
            }

            // Reset / Apply Buttons
            HorizontalDivider(color = DividerColor)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { resetAll() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Accent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent)
                ) { Text("Reset", fontWeight = FontWeight.SemiBold) }

                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) { Text("Apply Filters", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

// Functions to Reuse

@Composable
private fun FilterSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(vertical = 10.dp)) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun FilterDivider() = HorizontalDivider(color = DividerColor, thickness = 1.dp)

// Label for Min and Max (Price Range)
@Composable
private fun MinMaxLabel(min: String, max: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(min, fontSize = 11.sp, color = TextSecondary)
        Text(max, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun SelectionButtonRow(options: List<String>, selected: List<String>, onToggle: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = selected.contains(option)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) ButtonSelected else ButtonUnselected)
                    .border(1.dp, if (isSelected) Accent else Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { onToggle(option) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(option, fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else TextPrimary)
            }
        }
    }
}

@Composable
private fun MonthYearPicker(
    selectedMonth: String?,
    selectedYear: Int,
    onYearChange: (Int) -> Unit,
    onMonthSelect: (String) -> Unit
) {
    val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onClick = { onYearChange(selectedYear - 1) }) {
                Icon(Icons.Default.KeyboardArrowLeft, "Previous year", tint = Accent)
            }
            Text(selectedYear.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            IconButton(onClick = { onYearChange(selectedYear + 1) }) {
                Icon(Icons.Default.KeyboardArrowRight, "Next year", tint = Accent)
            }
        }
        Spacer(Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            months.chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { month ->
                        val isSelected = selectedMonth == month
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ButtonSelected else ButtonUnselected)
                                .clickable { onMonthSelect(month) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(month, fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextPrimary,
                                textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        Arrangement.SpaceBetween, Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = TextPrimary)
        Switch(checked = checked, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, checkedTrackColor = Accent,
                uncheckedThumbColor = Color.White, uncheckedTrackColor = DividerColor))
    }
}