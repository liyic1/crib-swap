package com.example.cribswap.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.cribswap.ui.listings.*

object ListingRoutes {
    const val FEED        = "listings/feed"
    const val DETAIL      = "listings/detail"
    const val CREATE      = "listings/create"
    const val MY_LISTINGS = "listings/mine"
}

fun NavGraphBuilder.listingsNavGraph(navController: NavHostController) {

    composable(ListingRoutes.FEED) {
        val vm: ListingViewModel = viewModel()
        ListingsScreen(
            viewModel = vm,
            onNavigateToDetail = { listing ->
                vm.selectListing(listing)   // ← set listing BEFORE navigating
                navController.navigate(ListingRoutes.DETAIL)
            },
            onNavigateToCreate = {
                navController.navigate(ListingRoutes.CREATE)
            }
        )
    }

    composable(ListingRoutes.DETAIL) {
        // Share the SAME ViewModel instance from the back stack
        val vm: ListingViewModel = navController
            .getBackStackEntry(ListingRoutes.FEED)
            .let { viewModel(it) }

        ListingDetailScreen(
            viewModel = vm,
            onNavigateBack = { navController.popBackStack() },
            onMessageOwner = { ownerId ->
                // navController.navigate("messaging/$ownerId")
            }
        )
    }

    composable(ListingRoutes.CREATE) {
        val vm: ListingViewModel = navController
            .getBackStackEntry(ListingRoutes.FEED)
            .let { viewModel(it) }

        CreateListingScreen(
            viewModel = vm,
            onNavigateBack  = { navController.popBackStack() },
            onSubmitSuccess = {
                navController.popBackStack(ListingRoutes.FEED, inclusive = false)
            }
        )
    }

    composable(ListingRoutes.MY_LISTINGS) {
        val vm: ListingViewModel = viewModel()
        MyListingsScreen(
            viewModel          = vm,
            onNavigateToCreate = { navController.navigate(ListingRoutes.CREATE) },
            onNavigateToDetail = { listing ->
                vm.selectListing(listing)
                navController.navigate(ListingRoutes.DETAIL)
            },
            onNavigateToEdit   = { }
        )
    }
}