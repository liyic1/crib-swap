package com.example.cribswap.ui.theme.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.cribswap.Chat
import com.example.cribswap.Home
import com.example.cribswap.ui.theme.navigation.NavItem
import com.example.cribswap.Profile
import com.example.cribswap.Saved

@Composable
fun NavigationBar(modifier: Modifier = Modifier) {
    val NavItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Saved", Icons.Default.Favorite),
        NavItem("Chat", Icons.Default.Create),
        NavItem("Profile", Icons.Default.Face)
    )
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar (containerColor = Color(0xFF2D6E8F)) {
                NavItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = "Icon")
                        },
                        label = {
                            Text(text = navItem.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF1A4F6E), // bubble shape behind selected button
                            selectedIconColor = Color.White, // icon when selected
                            unselectedIconColor = Color.White.copy(alpha = 0.6f), // when icon not selected
                            selectedTextColor = Color.White, // text when selected
                            unselectedTextColor = Color.White.copy(alpha = 0.6f) // when text not selected
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding), selectedIndex)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int) {
    when (selectedIndex) {
        0 -> Home(modifier = modifier)
        1 -> Saved(modifier = modifier)
        2 -> Chat(modifier = modifier)
        3 -> Profile(modifier = modifier)
    }
}