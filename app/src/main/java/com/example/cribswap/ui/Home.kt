package com.example.cribswap.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.ui.filter.FilterBottomSheet
import com.example.cribswap.ui.filter.FilterViewModel
import com.example.cribswap.ui.theme.CribSwapBlue

@Composable
fun Home(
    modifier: Modifier = Modifier,
    filterViewModel: FilterViewModel = viewModel()
) {
    var showFilter by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {

        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = { showFilter = true },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CribSwapBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CribSwapBlue),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    Icons.Default.Search, contentDescription = "Filter",
                    tint = CribSwapBlue, modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Filter", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        if (showFilter) {
            FilterBottomSheet(
                onDismiss = { showFilter = false },
                viewModel = filterViewModel
            )
        }
    }
}