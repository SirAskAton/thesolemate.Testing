package com.example.thesolemate

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.thesolemate.navigation.Screen
import com.example.thesolemate.navigation.SetupNavGraph
import com.example.thesolemate.utils.PrefManager

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val pref = PrefManager(context)

    val userId = pref.getUserId()

    Surface(color = MaterialTheme.colorScheme.background) {
        // Jika sudah login, langsung navigasi ke home
        if (userId != -1) {
            navController.navigate(Screen.Home.createRoute(userId)) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }

        SetupNavGraph(navController = navController, context = context)

    }
}
