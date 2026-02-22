package com.dev.expirytracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.dev.expirytracker.ui.login.LoginScreen
import com.dev.expirytracker.ui.login.RegisterScreen
import com.dev.expirytracker.ui.home.HomeScreen
import com.dev.expirytracker.ui.add.AddItemScreen
import com.dev.expirytracker.ui.detail.DetailScreen
import com.dev.expirytracker.ui.profile.ProfileScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }
        composable("add") {
            AddItemScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            DetailScreen(itemId, navController)
        }
    }
}