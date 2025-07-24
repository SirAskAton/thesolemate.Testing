package com.example.thesolemate

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.thesolemate.navigation.SetupNavGraph
import com.example.thesolemate.screen.*
import com.example.thesolemate.utils.PrefManager

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val pref = PrefManager(context)

    Surface(color = MaterialTheme.colorScheme.background) {
        SetupNavGraph(navController = navController)
    }
}

