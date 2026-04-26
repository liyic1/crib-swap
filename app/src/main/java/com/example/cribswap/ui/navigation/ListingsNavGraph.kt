package com.example.cribswap.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.cribswap.ui.filter.FilterViewModel
import com.example.cribswap.ui.listings.*

object ListingRoutes {
    const val FEED        = "listings/feed"
    const val DETAIL      = "listings/detail"
    const val CREATE      = "listings/create"
    const val MY_LISTINGS = "listings/mine"
}

fun NavGraphBuilder.listingsNavGraph(
    navController: NavHostController,
    onNavigateToChat: (String, String) -> Unit = { _, _ -> },
    sharedViewModel: ListingViewModel,
    filterViewModel: FilterViewModel  // 🔥 NEW: Accept FilterViewModel
) {
    composable(ListingRoutes.FEED) {
        ListingsScreen(
            viewModel = sharedViewModel,
            filterViewModel = filterViewModel,  // 🔥 NEW: Pass to screen
            onNavigateToDetail = { listing ->
                sharedViewModel.selectListing(listing)
                navController.navigate(ListingRoutes.DETAIL)
            },
            onNavigateToCreate = {
                navController.navigate(ListingRoutes.CREATE)
            }
        )
    }

    composable(ListingRoutes.DETAIL) {
        ListingDetailScreen(
            viewModel = sharedViewModel,
            onNavigateBack = { navController.popBackStack() },
            onMessageOwner = { ownerId ->
                val listing = sharedViewModel.selectedListing.value
                onNavigateToChat(
                    ownerId,
                    listing?.ownerName ?: "Seller"
                )
            }
        )
    }

    composable(ListingRoutes.CREATE) {
        CreateListingScreen(
            viewModel = sharedViewModel,
            onNavigateBack  = { navController.popBackStack() },
            onSubmitSuccess = {
                navController.popBackStack(ListingRoutes.FEED, inclusive = false)
            }
        )
    }

    composable(ListingRoutes.MY_LISTINGS) {
        MyListingsScreen(
            viewModel          = sharedViewModel,
            onNavigateToCreate = { navController.navigate(ListingRoutes.CREATE) },
            onNavigateToDetail = { listing ->
                sharedViewModel.selectListing(listing)
                navController.navigate(ListingRoutes.DETAIL)
            },
            onNavigateToEdit   = { }
        )
    }
}