package com.example.cribswap.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.ui.listings.*

// ── Routes ────────────────────────────────────────────────────────────────────

object ListingRoutes {
    const val FEED        = "listings/feed"
    const val DETAIL      = "listings/detail"
    const val CREATE      = "listings/create"
    const val MY_LISTINGS = "listings/mine"
}

// ── Nav graph extension ───────────────────────────────────────────────────────
//
// Add to your root NavHost:
//
//   NavHost(navController, startDestination = ListingRoutes.FEED) {
//       listingsNavGraph(navController)
//   }

fun NavGraphBuilder.listingsNavGraph(navController: NavHostController) {

    composable(ListingRoutes.FEED) {
        val vm: ListingViewModel = viewModel()
        ListingsScreen(
            viewModel = vm,
            onNavigateToDetail = { listing ->
                vm.selectListing(listing)
                navController.navigate(ListingRoutes.DETAIL)
            },
            onNavigateToCreate = { navController.navigate(ListingRoutes.CREATE) }
        )
    }

    composable(ListingRoutes.DETAIL) {
        val vm: ListingViewModel = viewModel()
        ListingDetailScreen(
            viewModel = vm,
            onNavigateBack = { navController.popBackStack() },
            onMessageOwner = { ownerId ->
                // navController.navigate("messaging/$ownerId")
            }
        )
    }

    composable(ListingRoutes.CREATE) {
        val vm: ListingViewModel = viewModel()
        CreateListingScreen(
            viewModel = vm,
            onNavigateBack  = { navController.popBackStack() },
            onSubmitSuccess = { navController.popBackStack(ListingRoutes.FEED, inclusive = false) }
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
            onNavigateToEdit   = { /* navController.navigate("listings/edit/${it.id}") */ }
        )
    }
}
