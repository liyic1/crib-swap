package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.example.cribswap.ui.components.BackButton
import com.example.cribswap.ui.components.SettingsItem

@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize().padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            BackButton { onBack() }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Settings", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Account Settings", color = Color.Gray, style = MaterialTheme.typography.labelMedium)

            SettingsItem(
                value = "Edit Profile",
                onClick = { CribSwapAppRouter.navigateTo(Screen.EditProfileScreen) }
            )
            SettingsItem(
                value = "Change Password",
                onClick = { CribSwapAppRouter.navigateTo(Screen.ChangePasswordScreen) }
            )
            SettingsItem(
                value = "Notifications",
                onClick = { CribSwapAppRouter.navigateTo(Screen.NotificationsScreen) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "More", color = Color.Gray, style = MaterialTheme.typography.labelMedium)

            SettingsItem(
                value = "About Us",
                onClick = { CribSwapAppRouter.navigateTo(Screen.AboutUsScreen) }
            )
            SettingsItem(
                value = "Privacy Policy",
                onClick = { CribSwapAppRouter.navigateTo(Screen.PrivacyPolicyScreen) }
            )
            SettingsItem(
                value = "Terms and Conditions",
                onClick = { CribSwapAppRouter.navigateTo(Screen.TermsScreen) }
            )
        }
    }
}
