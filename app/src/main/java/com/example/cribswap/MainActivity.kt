package com.example.cribswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.cribswap.ui.theme.CribSwapBlue
import com.example.cribswap.ui.theme.CribSwapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CribSwapTheme {
                FilterBottomSheet()
            }
        }
    }
}

@Composable
fun FilterBottomSheet() {
    // Filter sheet
    var showFilter by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            onClick = { showFilter = true },
            colors = ButtonDefaults.buttonColors(containerColor = CribSwapBlue)
        ) {
            Text("Filter Listings")
        }
    }

    if (showFilter) {
        FilterBottomSheet(onDismiss = { showFilter = false })
    }
}