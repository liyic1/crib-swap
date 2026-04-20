package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
    val user = profileViewModel.user.value
    val isLoading = profileViewModel.isLoading.value

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize().padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton { onBack() }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Personal Details", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (user == null) {
                Text("Could not load profile.", color = Color.Red)
            } else {
                Text("Full Name", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                ValueBox(user.displayName.ifBlank { "—" })

                Spacer(modifier = Modifier.height(16.dp))
                Text("Email", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                ValueBox(user.email.ifBlank { "—" })

                Spacer(modifier = Modifier.height(16.dp))
                Text("Phone Number", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                ValueBox(user.phoneNumber.ifBlank { "Not set" })

                Spacer(modifier = Modifier.height(16.dp))
                Text("Address", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                ValueBox(user.address.ifBlank { "Not set" })
            }
        }
    }
}
