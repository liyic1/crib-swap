package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.example.cribswap.ui.components.BackButton
import com.example.cribswap.ui.components.ValueBox

@Composable
fun PersonalDetailScreen() {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize().padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton {
                    CribSwapAppRouter.navigateTo(Screen.ProfileScreen)
                }
                Spacer(modifier = Modifier.width(26.dp))
                Text(text = "Personal Detail")
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Full Name")
            ValueBox("John Doe")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Username")
            ValueBox("Johnny12")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Email")
            ValueBox("Johndoe@umn.edu")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Phone Number")
            ValueBox("+1 234 567 8910")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Address")
            ValueBox("45 New Avenue, New York")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun DefaultPreviewOfPersonalDetailScreen() {
    PersonalDetailScreen()
}