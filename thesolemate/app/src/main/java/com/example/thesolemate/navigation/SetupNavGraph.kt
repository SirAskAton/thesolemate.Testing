package com.example.thesolemate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.thesolemate.data.remote.ApiClient
import com.example.thesolemate.data.repository.CartRepository
import com.example.thesolemate.data.repository.ShoeRepository
import com.example.thesolemate.screen.*

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val apiService = ApiClient.apiService
    val shoeRepository = remember { ShoeRepository(apiService) }
    val cartRepository = remember { CartRepository(apiService) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                shoeRepository = shoeRepository,
                cartRepository = cartRepository
            )
        }
        composable(Screen.Cart.route) {
            CartScreen(
                navController = navController,
                cartRepository = cartRepository // ✅ FIXED: sesuai parameter
            )
        }
        composable(
            route = Screen.ShoeDetail.route,
            arguments = listOf(navArgument("shoeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val shoeId = backStackEntry.arguments?.getInt("shoeId") ?: 0
            ShoeDetailScreen(
                navController = navController,
                shoeId = shoeId,
                shoeRepository = shoeRepository,
                cartRepository = cartRepository
            )
        }
    }
}
