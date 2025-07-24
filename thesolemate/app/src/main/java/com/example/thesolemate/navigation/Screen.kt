package com.example.thesolemate.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Receipt : Screen("receipt")
    object ShoeDetail : Screen("detail/{shoeId}") {
        fun createRoute(shoeId: Int): String = "detail/$shoeId"
    }
}