package com.capstone.attirely

import AddResult
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.capstone.attirely.data.OutfitData
import com.capstone.attirely.ui.add.AddScreen
import com.capstone.attirely.ui.home.HomeScreen
import com.capstone.attirely.ui.laoding.LoadingScreen
import com.capstone.attirely.ui.profile.ProfileScreen
import com.capstone.attirely.ui.search.SearchScreen
import com.capstone.attirely.ui.theme.AttirelyTheme

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("home") { HomeScreen() }
            composable("search") { SearchScreen() }
            composable("profile") { ProfileScreen(navController) }
            composable("add") { AddScreen(navController) }
            composable("loading_screen") { LoadingScreen() }
            composable("add_result") { backStackEntry ->
                val outfitData = backStackEntry.arguments?.getParcelableArrayList<OutfitData>("outfitData")
                AddResult(
                    navController = navController,
                    outfitData = outfitData ?: emptyList()
                )
            }
        }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        if (currentRoute != "add" && currentRoute != "loading_screen") {
            BottomNavBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val items = listOf(
        NavItem.Home,
        NavItem.Search,
        NavItem.Profile
    )
    Column(
        modifier = modifier
            .background(color = Color.Transparent)
            .padding(bottom = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomNavigation(
            backgroundColor = colorResource(id = R.color.secondary),
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .height(60.dp)
                .width(200.dp)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = colorResource(id = R.color.primary),
                                modifier = Modifier.size(24.dp)
                            )
                            if (currentRoute == item.route) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(2.dp)
                                        .background(color = colorResource(id = R.color.primary))
                                )
                            }
                        }
                    },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray,
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

sealed class NavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavItem("home", Icons.Filled.Home, "Home")
    object Search : NavItem("search", Icons.Filled.Search, "Search")
    object Profile : NavItem("profile", Icons.Filled.Person, "Profile")
}

@Preview
@Composable
fun PreviewMainScreen() {
    AttirelyTheme {
        MainScreen()
    }
}