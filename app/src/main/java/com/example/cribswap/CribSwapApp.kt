package com.example.cribswap

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.example.cribswap.ui.screen.ForgotPasswordScreen
import com.example.cribswap.ui.screen.HomeScreen
import com.example.cribswap.ui.screen.LoginScreen
import com.example.cribswap.ui.screen.PersonalDetailScreen
import com.example.cribswap.ui.screen.ProfileScreen
import com.example.cribswap.ui.screen.SettingsScreen
import com.example.cribswap.ui.screen.SignUpScreen

@Composable
fun CribSwapApp() {
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
                }            }

        }
    }
}