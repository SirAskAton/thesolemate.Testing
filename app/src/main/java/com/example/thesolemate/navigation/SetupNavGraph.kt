package com.example.thesolemate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.thesolemate.data.remote.ApiClient
import com.example.thesolemate.data.repository.ShoeRepository
import com.example.thesolemate.repository.CartRepository
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
        // Login
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        // Register
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        // Home
        composable(
            route = Screen.Home.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            HomeScreen(
                navController = navController,
                shoeRepository = shoeRepository,
                cartRepository = cartRepository,
                userId = userId
            )
        }

        // Cart
        composable(
            route = "cart/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            CartScreen(navController, userId)
        }



        // Shoe Detail
        composable(
            route = Screen.ShoeDetail.route,
            arguments = listOf(
                navArgument("shoeId") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val shoeId = backStackEntry.arguments?.getInt("shoeId") ?: -1
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            ShoeDetailScreen(
                navController = navController,
                shoeId = shoeId,
                userId = userId,
                shoeRepository = shoeRepository,
                cartRepository = cartRepository
            )
        }


        // Receipt (jika dibutuhkan, tambahkan implementasi di sini)
    }
}
