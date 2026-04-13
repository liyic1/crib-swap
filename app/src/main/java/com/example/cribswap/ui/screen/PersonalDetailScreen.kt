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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.ui.components.BackButton
import com.example.cribswap.ui.components.ValueBox
import com.example.cribswap.viewmodel.ProfileViewModel

@Composable
fun PersonalDetailScreen(
    onBack: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize().padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton { onBack() }
                Spacer(modifier = Modifier.width(26.dp))
                Text(text = "Personal Detail")
            }

            Spacer(modifier = Modifier.height(16.dp))

            val user = profileViewModel.currentUser.value

            Text("First Name")
            if (user == null || user.firstName.isEmpty()) {
                ValueBox("Not set")
            } else {
                ValueBox(user.firstName)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Last Name")
            if (user == null || user.lastName.isEmpty()) {
                ValueBox("Not set")
            } else {
                ValueBox(user.lastName)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Email")
            if (user == null || user.email.isEmpty()) {
                ValueBox("Not set")
            } else {
                ValueBox(user.email)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Phone Number")
            if (user == null || user.phoneNumber.isEmpty()) {
                ValueBox("Not set")
            } else {
                ValueBox(user.phoneNumber)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Address")
            if (user == null || user.address.isEmpty()) {
                ValueBox("Not set")
            } else {
                ValueBox(user.address)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
