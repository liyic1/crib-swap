package com.example.cribswap.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object LoginScreen : Screen()
    object SignUpScreen : Screen()
    object ForgotPasswordScreen : Screen()
    object EmailVerificationScreen : Screen()
    object ProfileScreen : Screen()
    object SettingsScreen : Screen()
    object PersonalDetailScreen : Screen()
    object HomeScreen : Screen()
    object EditProfileScreen : Screen()
    object ChangePasswordScreen : Screen()
    object NotificationsScreen : Screen()
    object AboutUsScreen : Screen()
    object PrivacyPolicyScreen : Screen()
    object TermsScreen : Screen()
    object MainScreen : Screen() // post-login: nav bar and filter
    object MessagesScreen : Screen()

    object PreferencesScreen : Screen()
}

object CribSwapAppRouter {
    private val backStack = mutableListOf<Screen>()

    var currentScreen: MutableState<Screen> =
        mutableStateOf(Screen.LoginScreen)

    fun navigateTo(destination: Screen) {
        if (currentScreen.value == destination) return

        backStack.add(currentScreen.value)
        currentScreen.value = destination
    }

    fun goBack() {
        if (backStack.isNotEmpty()) {
            currentScreen.value = backStack.removeAt(backStack.lastIndex)
        }
    }
}
