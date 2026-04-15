package com.example.cribswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.cribswap.ui.navigation.ListingRoutes
import com.example.cribswap.ui.navigation.listingsNavGraph
import com.example.cribswap.ui.theme.CribSwapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CribSwapTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = ListingRoutes.FEED
                ) {
                    listingsNavGraph(navController)
                }
            }
        }
    }
}