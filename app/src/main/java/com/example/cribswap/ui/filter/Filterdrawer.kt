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
import com.example.cribswap.ui.theme.CribSwapBlue
import com.example.cribswap.ui.theme.CribSwapBlueLight
import com.example.cribswap.ui.theme.DividerColor
import com.example.cribswap.ui.theme.SurfaceLight
import com.example.cribswap.ui.theme.TextPrimary
import com.example.cribswap.ui.theme.TextSecondary

private fun <T> List<T>.toggle(item: T): List<T> =
    if (contains(item)) this - item else this + item

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
                    color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterSection("Price Range") {
                    Box(Modifier.fillMaxWidth().height(22.dp)) {
                        Text(
                            "$${draft.priceMin.toInt()} – $${draft.priceMax.toInt()} / mo",
                            fontSize = 13.sp, color = CribSwapBlue, fontWeight = FontWeight.SemiBold
                        )
                    }
                    RangeSlider(
                        value = draft.priceMin..draft.priceMax,
                        onValueChange = {
                            viewModel.updateDraft { copy(priceMin = it.start, priceMax = it.endInclusive) }
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

                FilterSection("Location") {
                    OutlinedTextField(
                        value = draft.locationQuery,
                        onValueChange = { viewModel.updateDraft { copy(locationQuery = it) } },
                        placeholder = { Text("Address or zip code", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, null, tint = CribSwapBlue,
                                modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CribSwapBlue, unfocusedBorderColor = DividerColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth().height(22.dp)) {
                        Text(
                            "Within ${draft.distanceMiles.toInt()} mile${if (draft.distanceMiles > 1f) "s" else ""}",
                            fontSize = 13.sp, color = CribSwapBlue, fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = draft.distanceMiles,
                        onValueChange = { viewModel.updateDraft { copy(distanceMiles = it) } },
                        valueRange = 1f..50f, steps = 48,
                        colors = SliderDefaults.colors(
                            thumbColor = CribSwapBlue,
                            activeTrackColor = CribSwapBlue,
                            inactiveTrackColor = DividerColor
                        )
                    )
                    MinMaxLabel("1 mile", "50+ miles")
                }

                FilterDivider()

                FilterSection("Bedrooms") {
                    SelectionButtonRow(
                        options = listOf("Studio", "1", "2", "3", "4+"),
                        selected = draft.bedrooms,
                        onToggle = { viewModel.updateDraft { copy(bedrooms = bedrooms.toggle(it)) } }
                    )
                }

                FilterDivider()

                FilterSection("Bathrooms") {
                    SelectionButtonRow(
                        options = listOf("0.5", "1", "1.5", "2", "2.5", "3+"),
                        selected = draft.bathrooms,
                        onToggle = { viewModel.updateDraft { copy(bathrooms = bathrooms.toggle(it)) } }
                    )
                }

                FilterDivider()

                FilterSection("Roommates") {
                    SelectionButtonRow(
                        options = listOf("0", "1", "2", "3", "4+"),
                        selected = draft.roommates,
                        onToggle = { viewModel.updateDraft { copy(roommates = roommates.toggle(it)) } }
                    )
                }

                FilterDivider()

                FilterSection("Lease Term") {
                    Text("Start", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    MonthYearPicker(
                        selectedMonth = draft.leaseStartMonth,
                        selectedYear = draft.leaseStartYear,
                        onYearChange = { viewModel.updateDraft { copy(leaseStartYear = it) } },
                        onMonthSelect = {
                            viewModel.updateDraft {
                                copy(leaseStartMonth = if (leaseStartMonth == it) null else it)
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("End", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    MonthYearPicker(
                        selectedMonth = draft.leaseEndMonth,
                        selectedYear = draft.leaseEndYear,
                        onYearChange = { viewModel.updateDraft { copy(leaseEndYear = it) } },
                        onMonthSelect = {
                            viewModel.updateDraft {
                                copy(leaseEndMonth = if (leaseEndMonth == it) null else it)
                            }
                        }
                    )
                }

                FilterDivider()

                FilterSection("Building Type") {
                    listOf("Apartment", "Condo", "House").forEach { type ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.updateDraft { copy(buildingTypes = buildingTypes.toggle(type)) }
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = draft.buildingTypes.contains(type),
                                onCheckedChange = {
                                    viewModel.updateDraft { copy(buildingTypes = buildingTypes.toggle(type)) }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = CribSwapBlue)
                            )
                            Text(type, fontSize = 14.sp, color = TextPrimary)
                        }
                    }
                }

                FilterDivider()

                FilterSection("Amenities") {
                    ToggleRow("Furnished", draft.furnished) {
                        viewModel.updateDraft { copy(furnished = it) }
                    }
                    ToggleRow("In-Unit Laundry", draft.inUnitLaundry) {
                        viewModel.updateDraft { copy(inUnitLaundry = it) }
                    }
                    ToggleRow("Parking Included", draft.parking) {
                        viewModel.updateDraft { copy(parking = it) }
                    }
                    ToggleRow("Photos Required", draft.photosRequired) {
                        viewModel.updateDraft { copy(photosRequired = it) }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CribSwapBlue)
                ) { Text("Reset", fontWeight = FontWeight.SemiBold) }

                Button(
                    onClick = {
                        viewModel.applyFilters()
                        onDismiss()
                    },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CribSwapBlue)
                ) { Text("Apply Filters", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}


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
                    .border(1.dp, if (isSelected) CribSwapBlue else Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { onToggle(option) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    option, fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else TextPrimary
                )
            }
        }
    }
}

@Composable
fun MonthYearRangePicker(
    selectedStartMonth: String?,
    selectedEndMonth: String?,
    selectedYear: Int,
    onYearChange: (Int) -> Unit,
    onRangeChange: (start: String?, end: String?) -> Unit
) {
    val months = listOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )

    fun monthIndex(m: String?) = m?.let { months.indexOf(it) } ?: -1

    val startIndex = monthIndex(selectedStartMonth)
    val endIndex = monthIndex(selectedEndMonth)

    Column {

        // Select Year
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onYearChange(selectedYear - 1) }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Prev", tint = CribSwapBlue)
            }

            Text(
                text = selectedYear.toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            IconButton(onClick = { onYearChange(selectedYear + 1) }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next", tint = CribSwapBlue)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Select Month
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            months.chunked(4).forEach { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { month ->

                        val index = months.indexOf(month)

                        val isStart = selectedStartMonth == month
                        val isEnd = selectedEndMonth == month
                        val isInRange =
                            startIndex != -1 &&
                                    endIndex != -1 &&
                                    index in startIndex..endIndex

                        val bgColor = when {
                            isStart || isEnd -> CribSwapBlue
                            isInRange -> CribSwapBlue.copy(alpha = 0.25f)
                            else -> CribSwapBlueLight
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clickable {
                                    onRangeChange(selectedStartMonth, selectedEndMonth).let {
                                        when {
                                            selectedStartMonth == null -> {
                                                onRangeChange(month, null)
                                            }

                                            selectedEndMonth == null -> {
                                                val start = selectedStartMonth!!

                                                val startIdx = months.indexOf(start)
                                                val clickedIdx = index

                                                if (clickedIdx < startIdx) {
                                                    onRangeChange(month, null)
                                                } else {
                                                    onRangeChange(start, month)
                                                }
                                            }

                                            else -> {
                                                onRangeChange(month, null)
                                            }
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = bgColor
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = month,
                                    fontSize = 12.sp,
                                    fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isStart || isEnd) Color.White else TextPrimary
                                )
                            }
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
        Switch(
            checked = checked, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, checkedTrackColor = CribSwapBlue,
                uncheckedThumbColor = Color.White, uncheckedTrackColor = DividerColor
            )
        )
    }
}