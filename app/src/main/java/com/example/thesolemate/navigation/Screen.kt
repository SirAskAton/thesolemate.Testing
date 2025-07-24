package com.example.thesolemate.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")
    object Register : Screen("register")

    object Home : Screen("home/{userId}") {
        fun createRoute(userId: Int): String = "home/$userId"
    }

    object Cart : Screen("cart/{userId}") {
        fun createRoute(userId: Int): String = "cart/$userId"
    }

    object Receipt : Screen("receipt")

    object ShoeDetail : Screen("detail/{shoeId}/{userId}") {
        fun createRoute(shoeId: Int, userId: Int): String = "detail/$shoeId/$userId"
    }
}
