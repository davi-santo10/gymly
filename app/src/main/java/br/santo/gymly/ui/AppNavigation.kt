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
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.santo.gymly.features.friends.FriendsScreen
import br.santo.gymly.features.home.HomeScreen
import br.santo.gymly.features.progress.ProgressScreen
import br.santo.gymly.features.routines.activeworkout.ui.ActiveWorkoutScreen
import br.santo.gymly.features.routines.ui.createroutine.CreateRoutineScreen
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui.ExercisesScreen
import br.santo.gymly.features.routines.ui.details.RoutineDetailsScreen
import br.santo.gymly.features.routines.ui.routinelist.RoutinesScreen

@Composable
fun GymlyApp(modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    NavigationSuite(
        modifier = modifier,
        windowSizeClass = windowSizeClass.widthSizeClass,
        navController = navController
    )
}

@Composable
fun NavigationSuite(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowWidthSizeClass,
    navController: NavHostController
) {
    val navigationItems = listOf(Screen.Home, Screen.Routines, Screen.Progress, Screen.Friends)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isCompact = windowSizeClass == WindowWidthSizeClass.Compact

    if (isCompact) {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    navigationItems.forEach { screen ->
                        NavigationBarItem(
                            selected = (currentRoute == screen.route),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        Row(Modifier.fillMaxSize()) {
            NavigationRail {
                navigationItems.forEach { screen ->
                    NavigationRailItem(
                        selected = (currentRoute == screen.route),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) }
                    )
                }
            }
            AppNavHost(
                navController = navController,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(
            route = Screen.Exercises.route,
            arguments = listOf(navArgument("initialIds") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            // The ExercisesScreen doesn't need the parameter passed directly
            // because Hilt handles the SavedStateHandle in the ViewModel.
            ExercisesScreen(navController = navController)
        }
        composable(Screen.Progress.route) { ProgressScreen() }
        composable(Screen.Friends.route) { FriendsScreen() }


        composable(Screen.Routines.route) {
            RoutinesScreen(navController = navController)
        }
        composable(Screen.CreateRoutine.route) {
            CreateRoutineScreen(navController = navController)
        }
        composable(
            route = Screen.RoutineDetails.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
        ) {

            RoutineDetailsScreen(navController = navController)
        }
        composable (
            route = Screen.ActiveWorkout.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType})
        ) {
            ActiveWorkoutScreen(navController = navController)
        }
    }
}