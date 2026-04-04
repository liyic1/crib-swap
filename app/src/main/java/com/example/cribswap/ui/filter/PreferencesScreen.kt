package com.example.cribswap.ui.filter  // ← was ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
// ↑ no more import for onboarding package — everything is in ui.filter now

@Composable
fun PreferencesScreen(
    filterViewModel: FilterViewModel,
    preferencesViewModel: PreferencesViewModel = viewModel()
) {
    val prefs by preferencesViewModel.prefs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CribSwapBlue)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column {
                Text(
                    "Set Your Preference",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Form
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Price Range
            PrefsSection(
                title = "Price Range",
                subtitle = "What is your ideal rent price?"
            ) {
                Text(
                    "$${prefs.priceMin.toInt()} – $${prefs.priceMax.toInt()} / mo",
                    fontSize = 14.sp, color = CribSwapBlue, fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                RangeSlider(
                    value = prefs.priceMin..prefs.priceMax,
                    onValueChange = {
                        preferencesViewModel.updatePrefs {
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
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("$0", fontSize = 11.sp, color = TextSecondary)
                    Text("$5000+", fontSize = 11.sp, color = TextSecondary)
                }
            }

            HorizontalDivider(color = DividerColor)

            // Location
            PrefsSection(
                title = "Location",
                subtitle = "Where are you looking to live?"
            ) {
                OutlinedTextField(
                    value = prefs.locationQuery,
                    onValueChange = {
                        preferencesViewModel.updatePrefs { copy(locationQuery = it) }
                    },
                    placeholder = { Text("Address or zip code", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, null,
                            tint = CribSwapBlue, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CribSwapBlue,
                        unfocusedBorderColor = DividerColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Within ${prefs.distanceMiles.toInt()} mile${if (prefs.distanceMiles > 1f) "s" else ""}",
                    fontSize = 14.sp, color = CribSwapBlue, fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Slider(
                    value = prefs.distanceMiles,
                    onValueChange = {
                        preferencesViewModel.updatePrefs { copy(distanceMiles = it) }
                    },
                    valueRange = 1f..50f, steps = 48,
                    colors = SliderDefaults.colors(
                        thumbColor = CribSwapBlue,
                        activeTrackColor = CribSwapBlue,
                        inactiveTrackColor = DividerColor
                    )
                )
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("1 mile", fontSize = 11.sp, color = TextSecondary)
                    Text("50+ miles", fontSize = 11.sp, color = TextSecondary)
                }
            }

            HorizontalDivider(color = DividerColor)

            // Lease Term
            PrefsSection(
                title = "Lease Term",
                subtitle = "How long do you plan to stay?"
            ) {
                Text("Start", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                MonthYearPicker(
                    selectedMonth = prefs.leaseStartMonth,
                    selectedYear = prefs.leaseStartYear,
                    onYearChange = {
                        preferencesViewModel.updatePrefs { copy(leaseStartYear = it) }
                    },
                    onMonthSelect = {
                        preferencesViewModel.updatePrefs {
                            copy(leaseStartMonth = if (leaseStartMonth == it) null else it)
                        }
                    }
                )
                Spacer(Modifier.height(16.dp))
                Text("End", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                MonthYearPicker(
                    selectedMonth = prefs.leaseEndMonth,
                    selectedYear = prefs.leaseEndYear,
                    onYearChange = {
                        preferencesViewModel.updatePrefs { copy(leaseEndYear = it) }
                    },
                    onMonthSelect = {
                        preferencesViewModel.updatePrefs {
                            copy(leaseEndMonth = if (leaseEndMonth == it) null else it)
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        // Get Started — pinned to bottom
        HorizontalDivider(color = DividerColor)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = { preferencesViewModel.completeOnboarding(filterViewModel) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CribSwapBlue)
            ) {
                Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

@Composable
private fun PrefsSection(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, fontSize = 13.sp, color = TextSecondary)
        Spacer(Modifier.height(14.dp))
        content()
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
                Icon(Icons.Default.KeyboardArrowLeft, "Previous year", tint = CribSwapBlue)
            }
            Text(selectedYear.toString(), fontSize = 15.sp,
                fontWeight = FontWeight.Bold, color = TextPrimary)
            IconButton(onClick = { onYearChange(selectedYear + 1) }) {
                Icon(Icons.Default.KeyboardArrowRight, "Next year", tint = CribSwapBlue)
            }
        }
        Spacer(Modifier.height(8.dp))
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
                                .background(if (isSelected) CribSwapBlue else CribSwapBlueLight)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                month, fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}