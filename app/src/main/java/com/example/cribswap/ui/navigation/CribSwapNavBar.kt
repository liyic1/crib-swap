package com.example.cribswap.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.cribswap.ui.Chat
import com.example.cribswap.ui.Home
import com.example.cribswap.ui.Saved
import com.example.cribswap.ui.conversation.MessagesScreen
import com.example.cribswap.ui.filter.FilterViewModel
import com.example.cribswap.ui.listings.ListingViewModel
import com.example.cribswap.ui.screen.PersonalDetailScreen
import com.example.cribswap.ui.screen.ProfileScreen
import com.example.cribswap.ui.screen.SettingsScreen
import com.example.cribswap.ui.theme.NavBarBackground
import com.example.cribswap.ui.theme.NavBarContentSelected
import com.example.cribswap.ui.theme.NavBarContentUnselected
import com.example.cribswap.ui.theme.NavBarIndicator

private val navItemList = listOf(
    NavItem("Home", Icons.Default.Home),
    NavItem("Saved", Icons.Default.Favorite),
    NavItem("Listings", Icons.Default.List),
    NavItem("Chat", Icons.Default.Create),
    NavItem("Profile", Icons.Default.Face)
)

@Composable
fun CribSwapNavBar(
    modifier: Modifier = Modifier,
    filterViewModel: FilterViewModel = viewModel()
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var profileSubScreen by rememberSaveable { mutableStateOf(ProfileSubScreen.Profile.name) }
    var pendingMessageUserId by rememberSaveable { mutableStateOf("") }
    var pendingMessageUserName by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(containerColor = NavBarBackground) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            if (index == 4) profileSubScreen = ProfileSubScreen.Profile.name
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
        val listingsViewModel: ListingViewModel = viewModel()

        when (selectedIndex) {
            0 -> Home(
                modifier = Modifier.padding(innerPadding),
                filterViewModel = filterViewModel
            )
            1 -> Saved(
                modifier = Modifier.padding(innerPadding),
                listingViewModel = listingsViewModel
            )
            2 -> {
                val listingsNavController = rememberNavController()
                NavHost(
                    navController = listingsNavController,
                    startDestination = ListingRoutes.FEED,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    listingsNavGraph(
                        navController = listingsNavController,
                        onNavigateToChat = { userId, userName ->
                            pendingMessageUserId = userId
                            pendingMessageUserName = userName
                            selectedIndex = 3
                        },
                        sharedViewModel = listingsViewModel
                    )
                }
            }
            3 -> MessagesScreen(
                modifier = Modifier.padding(innerPadding),
                startWithUserId = pendingMessageUserId.ifBlank { null },
                startWithUserName = pendingMessageUserName.ifBlank { null }
            )
            4 -> when (profileSubScreen) {
                ProfileSubScreen.Profile.name -> ProfileScreen(
                    onNavigateToSettings = { profileSubScreen = ProfileSubScreen.Settings.name },
                    onNavigateToPersonalDetail = { profileSubScreen = ProfileSubScreen.PersonalDetail.name }
                )
                ProfileSubScreen.Settings.name -> SettingsScreen(
                    onBack = { profileSubScreen = ProfileSubScreen.Profile.name }
                )
                ProfileSubScreen.PersonalDetail.name -> PersonalDetailScreen(
                    onBack = { profileSubScreen = ProfileSubScreen.Profile.name }
                )
            }
            else -> Home(
                modifier = Modifier.padding(innerPadding),
                filterViewModel = filterViewModel
            )
        }
    }
}