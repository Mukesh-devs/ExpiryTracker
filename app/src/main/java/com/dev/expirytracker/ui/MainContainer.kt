package com.dev.expirytracker.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.dev.expirytracker.ui.about.AboutScreen
import com.dev.expirytracker.ui.add.AddItemScreen
import com.dev.expirytracker.ui.detail.DetailScreen
import com.dev.expirytracker.ui.expired.ExpiredItemsScreen
import com.dev.expirytracker.ui.home.HomeScreen
import com.dev.expirytracker.ui.invitations.InvitationsScreen
import com.dev.expirytracker.ui.login.LoginScreen
import com.dev.expirytracker.ui.login.RegisterScreen
import com.dev.expirytracker.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private data class DrawerItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

private val drawerItems = listOf(
    DrawerItem("home", "Home", Icons.Outlined.Home, Icons.Outlined.Home),
    DrawerItem("expired", "Expired Items", Icons.Outlined.DeleteSweep, Icons.Outlined.DeleteSweep),
    DrawerItem("add", "Add Item", Icons.Outlined.AddCircleOutline, Icons.Outlined.AddCircleOutline),
    DrawerItem("invitations", "Invitations", Icons.Outlined.MailOutline, Icons.Outlined.MailOutline),
    DrawerItem("profile", "Profile", Icons.Outlined.Person, Icons.Outlined.Person),
    DrawerItem("about", "About", Icons.Outlined.Info, Icons.Outlined.Info)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {

    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) "home" else "login"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val showChrome = currentRoute !in listOf("login", "register")

    val accentColor = Color(0xFF1565C0)

    if (showChrome) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                    drawerContainerColor = Color.White
                ) {
                    // Drawer Header
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Expiry Tracker",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                    auth.currentUser?.email?.let { email ->
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF90A4AE),
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = Color(0xFFEEEEEE)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    drawerItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    item.label,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.label
                                )
                            },
                            selected = selected,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { inclusive = item.route == "home" }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = accentColor.copy(alpha = 0.1f),
                                selectedIconColor = accentColor,
                                selectedTextColor = accentColor,
                                unselectedIconColor = Color(0xFF78909C),
                                unselectedTextColor = Color(0xFF546E7A)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = Color(0xFFEEEEEE)
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                "Logout",
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = "Logout",
                                tint = Color(0xFFE53935)
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            auth.signOut()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Expiry Tracker",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.3).sp
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = accentColor
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFFF5F9FF),
                            titleContentColor = Color(0xFF0D47A1)
                        )
                    )
                },
                floatingActionButton = {
                    if (currentRoute == "home") {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("add") {
                                    launchSingleTop = true
                                }
                            },
                            containerColor = accentColor,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Item",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                containerColor = Color(0xFFF5F9FF)
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(padding),
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInHorizontally(
                                    initialOffsetX = { it / 4 },
                                    animationSpec = tween(300)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(200))
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInHorizontally(
                                    initialOffsetX = { -it / 4 },
                                    animationSpec = tween(300)
                                )
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(200)) +
                                slideOutHorizontally(
                                    targetOffsetX = { it / 4 },
                                    animationSpec = tween(200)
                                )
                    }
                ) {
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("home") { HomeScreen(navController) }
                    composable("expired") { ExpiredItemsScreen(navController) }
                    composable("add") { AddItemScreen(navController) }
                    composable("invitations") { InvitationsScreen(navController) }
                    composable("profile") { ProfileScreen(navController) }
                    composable("about") { AboutScreen() }
                    composable("detail/{itemId}/{ownerId}") { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                        val ownerId = backStackEntry.arguments?.getString("ownerId") ?: ""
                        DetailScreen(itemId, ownerId, navController)
                    }
                }
            }
        }
    } else {
        // No drawer for login/register screens
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            }
        ) {

            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("expired") { ExpiredItemsScreen(navController) }
            composable("add") { AddItemScreen(navController) }
            composable("invitations") { InvitationsScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable("about") { AboutScreen() }

            composable("detail/{itemId}/{ownerId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                val ownerId = backStackEntry.arguments?.getString("ownerId") ?: ""
                DetailScreen(itemId, ownerId, navController)
            }
        }
    }
}