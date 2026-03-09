package com.example.cribswap.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object LoginScreen : Screen()
    object SignUpScreen : Screen()
    object ForgotPasswordScreen : Screen()
    object ProfileScreen : Screen()
    object SettingsScreen : Screen()
    object PersonalDetailScreen : Screen()
}

object CribSwapAppRouter {

    var currentScreen: MutableState<Screen> =
        mutableStateOf(Screen.ProfileScreen)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}