package com.dev.expirytracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import com.dev.expirytracker.ui.add.AddItemScreen
import com.dev.expirytracker.ui.detail.DetailScreen
import com.dev.expirytracker.ui.home.HomeScreen
import com.dev.expirytracker.ui.login.LoginScreen
import com.dev.expirytracker.ui.login.RegisterScreen
import com.dev.expirytracker.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {

    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) "home" else "login"

    val topBarColor = Color(0xFF2196F3)
    val bottomBarColor = Color(0xFF42A5F5)

    val showBottomBar = currentRoute !in listOf("login", "register")

    Scaffold(
        topBar = {
            if (currentRoute !in listOf("login", "register")) {
                TopAppBar(
                    title = {
                        Text(
                            "Expiry Tracker",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topBarColor,
                        titleContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {

            when {

//                currentRoute?.startsWith("detail") == true -> {
//
//                    NavigationBar(
//                        containerColor = bottomBarColor
//                    ) {
//
//                        NavigationBarItem(
//                            selected = false,
//                            onClick = { navController.popBackStack() },
//                            icon = { Text("⬅", color = Color.White) },
//                            label = { Text("Back", color = Color.White) },
//                            colors = NavigationBarItemDefaults.colors(
//                                selectedIconColor = Color.White,
//                                unselectedIconColor = Color.White,
//                                selectedTextColor = Color.White,
//                                unselectedTextColor = Color.White,
//                                indicatorColor = Color(0xFF42A5F5)
//                            )
//                        )
//
//                        NavigationBarItem(
//                            selected = false,
//                            onClick = { /* delete logic */ },
//                            icon = { Text("🗑", color = Color.White) },
//                            label = { Text("Delete", color = Color.White) },
//                            colors = NavigationBarItemDefaults.colors(
//                                selectedIconColor = Color.White,
//                                unselectedIconColor = Color.White,
//                                selectedTextColor = Color.White,
//                                unselectedTextColor = Color.White,
//                                indicatorColor = Color(0xFF42A5F5)
//                            )
//                        )
//                    }
//                }

                showBottomBar -> {

                    NavigationBar(
                        containerColor = bottomBarColor
                    ) {

                        NavigationBarItem(
                            selected = currentRoute == "home",
                            onClick = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            icon = { Text("🏠", color = Color.White) },
                            label = { Text("Home", color = Color.White) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White,
                                indicatorColor = Color(0xFF42A5F5)
                            )
                        )

                        NavigationBarItem(
                            selected = currentRoute == "profile",
                            onClick = { navController.navigate("profile") },
                            icon = { Text("👤", color = Color.White) },
                            label = { Text("Profile", color = Color.White) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White,
                                indicatorColor = Color(0xFF42A5F5)
                            )
                        )

                        NavigationBarItem(
                            selected = currentRoute == "add",
                            onClick = { navController.navigate("add") },
                            icon = { Text("+", color = Color.White) },
                            label = { Text("Add", color = Color.White) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White,
                                indicatorColor = Color(0xFF42A5F5)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {

            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("add") { AddItemScreen(navController) }
            composable("profile") { ProfileScreen(navController) }

            composable("detail/{itemId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                DetailScreen(itemId, navController)
            }
        }
    }
}