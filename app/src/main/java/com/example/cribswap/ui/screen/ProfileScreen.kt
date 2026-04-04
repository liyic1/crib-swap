package com.example.cribswap.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.example.cribswap.ui.components.ProfileItems
import com.example.cribswap.ui.components.ProfilePicture

@Composable
fun ProfileScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .size(90.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "John Doe",
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            ProfilePicture("2", "Listings")
            ProfilePicture("0", "Leased")
            ProfilePicture("1", "Reviews")

        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        ProfileItems(
            title = "Personal details",
            icon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            onClick = { CribSwapAppRouter.navigateTo(Screen.SettingsScreen) }
        )

        ProfileItems(
            title = "Settings",
            icon = {
                Icon(Icons.Default.Settings, contentDescription = null)
            },
            onClick = { CribSwapAppRouter.navigateTo(Screen.PersonalDetailScreen) }
        )
    }
}

@Preview
@Composable
fun DefaultPreviewOfProfileScreen() {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = CribSwapAppRouter.currentScreen) { currentState ->
            when (currentState.value) {
                is Screen.ProfileScreen -> {
                    ProfileScreen()
                }
                is Screen.SettingsScreen -> {
                    SettingsScreen()
                }

                is Screen.PersonalDetailScreen -> {
                    PersonalDetailScreen()
                }
                is Screen.SignUpScreen -> {
                    SignUpScreen()
                }
                is Screen.LoginScreen -> {
                    LoginScreen()
                }

                is Screen.ForgotPasswordScreen -> {
                    ForgotPasswordScreen()
                }

                is Screen.HomeScreen -> {
                    HomeScreen()
                }
            }

        }
    }
}