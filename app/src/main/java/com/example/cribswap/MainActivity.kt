package com.example.cribswap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.ui.filter.FilterViewModel
import com.example.cribswap.ui.filter.PreferencesScreen
import com.example.cribswap.ui.filter.PreferencesViewModel
import com.example.cribswap.ui.navigation.CribSwapNavBar
import com.example.cribswap.ui.theme.CribSwapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CribSwapTheme {
                val filterViewModel: FilterViewModel = viewModel()
                val preferencesViewModel: PreferencesViewModel = viewModel()
                val onboardingComplete by preferencesViewModel.onboardingComplete
                    .collectAsStateWithLifecycle()

                if (onboardingComplete) {
                    CribSwapNavBar(filterViewModel = filterViewModel)
                } else {
                    PreferencesScreen(
                        filterViewModel = filterViewModel,
                        preferencesViewModel = preferencesViewModel
                    )
                }
            }
        }
    }
}
