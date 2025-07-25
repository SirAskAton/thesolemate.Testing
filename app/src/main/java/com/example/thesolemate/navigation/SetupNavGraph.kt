package com.example.thesolemate.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thesolemate.data.remote.ApiClient
import com.example.thesolemate.data.repository.ShoeRepository
import com.example.thesolemate.repository.CartRepository
import com.example.thesolemate.screen.*
import com.example.thesolemate.session.SessionManager


@Composable
fun SetupNavGraph(navController: NavHostController, context: Context) {
    val apiService = ApiClient.apiService
    val shoeRepository = remember { ShoeRepository(apiService) }
    val cartRepository = remember { CartRepository(apiService) }

    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

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
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                shoeRepository = shoeRepository,
                cartRepository = cartRepository,
                user_id = userId
            )
        }

        // Cart
        composable(Screen.Cart.route) {
            CartScreen(navController)
        }

        // Shoe Detail

        composable(Screen.ShoeDetail.route) { backStackEntry ->
            val shoeId = backStackEntry.arguments?.getString("shoeId")?.toIntOrNull() ?: 0
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0

            ShoeDetailScreen(
                shoeId = shoeId,
                navController = navController,
                userId = userId,
                shoeRepository = shoeRepository,
                cartRepository = cartRepository
            )
        }


        // Tambahkan halaman lainnya seperti Receipt jika ada
    }
}
