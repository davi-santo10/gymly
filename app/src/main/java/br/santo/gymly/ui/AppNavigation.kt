package br.santo.gymly.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import br.santo.gymly.ui.exercises.ExercisesScreen
import br.santo.gymly.ui.home.HomeScreen
import br.santo.gymly.ui.friends.FriendsScreen
import br.santo.gymly.ui.progress.ProgressScreen
import br.santo.gymly.ui.routines.RoutinesScreen
import androidx.navigation.compose.composable
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun GymlyApp(modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass) {
    NavigationSuite(modifier = modifier, windowSizeClass = windowSizeClass.widthSizeClass)
}

@Composable
fun NavigationSuite(modifier: Modifier = Modifier, windowSizeClass: WindowWidthSizeClass){
    val navController = rememberNavController()
    val navigationItems = listOf(Screen.Home, Screen.Routines, Screen.Exercises, Screen.Progress, Screen.Friends)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (windowSizeClass == WindowWidthSizeClass.Compact) {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                NavigationBar (windowInsets = NavigationBarDefaults.windowInsets) {
                    navigationItems.forEach { screen ->
                        NavigationBarItem(
                            selected = (currentRoute == screen.route),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Routines.route) { RoutinesScreen() }
                composable(Screen.Exercises.route) { ExercisesScreen() }
                composable(Screen.Progress.route) { ProgressScreen() }
                composable(Screen.Friends.route) { FriendsScreen() }
            }
        }
    } else {
        Row(Modifier.fillMaxSize()) {
            NavigationRail {
                navigationItems.forEach { screen ->
                    NavigationRailItem(
                        selected = (currentRoute == screen.route),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) }
                    )
                }
            }
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Routines.route) { RoutinesScreen() }
                composable(Screen.Exercises.route) { ExercisesScreen() }
                composable(Screen.Progress.route) { ProgressScreen() }
                composable(Screen.Friends.route) { FriendsScreen() }
            }
        }
    }
}