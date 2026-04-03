package com.example.cribswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cribswap.ui.theme.CribSwapTheme
import com.example.cribswap.ui.navigation.CribSwapNavBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CribSwapTheme {
                CribSwapNavBar()
            }
        }
    }
}

