package com.example.cribswap.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object LoginScreen : Screen()
    object SignUpScreen : Screen()
    object ForgotPasswordScreen : Screen()
}

object CribSwapAppRouter {

    var currentScreen: MutableState<Screen> =
        mutableStateOf(Screen.LoginScreen)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}