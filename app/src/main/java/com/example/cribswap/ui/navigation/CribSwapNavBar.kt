package com.example.cribswap.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.ui.Chat
import com.example.cribswap.ui.Home
import com.example.cribswap.ui.Saved
import com.example.cribswap.ui.filter.FilterViewModel
import com.example.cribswap.ui.screen.PersonalDetailScreen
import com.example.cribswap.ui.screen.ProfileScreen
import com.example.cribswap.ui.screen.SettingsScreen
import com.example.cribswap.ui.conversation.MessagesScreen
import com.example.cribswap.ui.theme.NavBarBackground
import com.example.cribswap.ui.theme.NavBarContentSelected
import com.example.cribswap.ui.theme.NavBarContentUnselected
import com.example.cribswap.ui.theme.NavBarIndicator


private val navItemList = listOf(
    NavItem("Home", Icons.Default.Home),
    NavItem("Saved", Icons.Default.Favorite),
    NavItem("Chat", Icons.Default.Create),
    NavItem("Profile", Icons.Default.Face)
)

@Composable
fun CribSwapNavBar(
    modifier: Modifier = Modifier,
    filterViewModel: FilterViewModel = viewModel()
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var profileSubScreen by remember { mutableStateOf(ProfileSubScreen.Profile) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(containerColor = NavBarBackground) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            if (index == 3) profileSubScreen = ProfileSubScreen.Profile
                        },
                        icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) },
                        label = { Text(text = navItem.label) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = NavBarIndicator,
                            selectedIconColor = NavBarContentSelected,
                            unselectedIconColor = NavBarContentUnselected,
                            selectedTextColor = NavBarContentSelected,
                            unselectedTextColor = NavBarContentUnselected
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            filterViewModel = filterViewModel,
            profileSubScreen = profileSubScreen,
            onProfileNavigate = { profileSubScreen = it }
        )
    }
}

@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    filterViewModel: FilterViewModel,
    profileSubScreen: ProfileSubScreen,
    onProfileNavigate: (ProfileSubScreen) -> Unit
) {
    when (selectedIndex) {
        0 -> Home(modifier = modifier, filterViewModel = filterViewModel)
        1 -> Saved(modifier = modifier)
        2 -> MessagesScreen(modifier = modifier)
        3 -> when (profileSubScreen) {
            ProfileSubScreen.Profile -> ProfileScreen(
                onNavigateToSettings = { onProfileNavigate(ProfileSubScreen.Settings) },
                onNavigateToPersonalDetail = { onProfileNavigate(ProfileSubScreen.PersonalDetail) }
            )
            ProfileSubScreen.Settings -> SettingsScreen(
                onBack = { onProfileNavigate(ProfileSubScreen.Profile) }
            )
            ProfileSubScreen.PersonalDetail -> PersonalDetailScreen(
                onBack = { onProfileNavigate(ProfileSubScreen.Profile) }
            )
        }
        else -> Home(modifier = modifier, filterViewModel = filterViewModel)
    }
}
