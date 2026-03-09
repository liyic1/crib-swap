package com.example.cribswap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun Home(modifier: Modifier = Modifier) {
    Column (
        modifier = modifier.fillMaxSize()
            .background(Color(0xFFE8F3F8)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
        Text(
            text = "Home/Listings Page",
            fontSize = 40.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E4F6A)
        )
    }
}