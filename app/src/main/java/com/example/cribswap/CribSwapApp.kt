package com.example.cribswap

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.example.cribswap.ui.conversation.MessagesScreen
import com.example.cribswap.ui.filter.FilterViewModel
import com.example.cribswap.ui.filter.PreferencesScreen
import com.example.cribswap.ui.filter.PreferencesViewModel
import com.example.cribswap.ui.filter.PreferencesViewModelFactory
import com.example.cribswap.ui.navigation.CribSwapNavBar
import com.example.cribswap.ui.screen.*

@Composable
fun CribSwapApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = CribSwapAppRouter.currentScreen) { currentState ->
            when (currentState.value) {

                is Screen.LoginScreen -> LoginScreen()
                is Screen.SignUpScreen -> SignUpScreen()
                is Screen.ForgotPasswordScreen -> ForgotPasswordScreen()

                // Temporary messaging test screen.
                is Screen.MessagesScreen -> MessagesScreen()

                is Screen.ProfileScreen -> ProfileScreen(
                    onNavigateToSettings = {
                        CribSwapAppRouter.navigateTo(Screen.SettingsScreen)
                    },
                    onNavigateToPersonalDetail = {
                        CribSwapAppRouter.navigateTo(Screen.PersonalDetailScreen)
                    }
                )

                is Screen.SettingsScreen -> SettingsScreen(
                    onBack = { CribSwapAppRouter.navigateTo(Screen.MainScreen) }
                )

                is Screen.PersonalDetailScreen -> PersonalDetailScreen(
                    onBack = { CribSwapAppRouter.navigateTo(Screen.MainScreen) }
                )

                is Screen.HomeScreen -> HomeScreen()

                is Screen.ChangePasswordScreen -> ChangePasswordScreen(
                    onBack = { CribSwapAppRouter.navigateTo(Screen.SettingsScreen) }
                )

                is Screen.EditProfileScreen -> EditProfileScreen(
                    onBack = { CribSwapAppRouter.navigateTo(Screen.SettingsScreen) }
                )

                is Screen.EmailVerificationScreen -> EmailVerificationScreen()

                is Screen.NotificationsScreen -> NotificationsScreen(
                    onBack = { CribSwapAppRouter.navigateTo(Screen.SettingsScreen) }
                )

                is Screen.PrivacyPolicyScreen -> PrivacyPolicyScreen(
                    onBack = { CribSwapAppRouter.navigateTo(Screen.SettingsScreen) }
                )

                is Screen.TermsScreen -> TermsScreen(
                    onBack = { CribSwapAppRouter.navigateTo(Screen.SettingsScreen) }
                )

                is Screen.AboutUsScreen -> AboutUsScreen(
                    onBack = { CribSwapAppRouter.navigateTo(Screen.SettingsScreen) }
                )

                is Screen.PreferencesScreen -> {
                    val filterViewModel: FilterViewModel = viewModel()
                    val preferencesViewModel: PreferencesViewModel = viewModel(
                        factory = PreferencesViewModelFactory(filterViewModel)
                    )

                    PreferencesScreen(
                        preferencesViewModel = preferencesViewModel,
                        onComplete = {
                            CribSwapAppRouter.navigateTo(Screen.MainScreen)
                        }
                    )
                }

                is Screen.MainScreen -> {
                    val filterViewModel: FilterViewModel = viewModel()
                    CribSwapNavBar(filterViewModel = filterViewModel)
                }
            }
        }
    }
}
