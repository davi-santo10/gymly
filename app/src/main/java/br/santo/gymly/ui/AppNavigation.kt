package br.santo.gymly.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
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

// Helper function to determine if navigation is hierarchical (should be animated)
private fun isHierarchicalNavigation(from: String?, to: String?): Boolean {
    val mainScreenRoutes = setOf(
        Screen.Home.route,
        Screen.Routines.route,
        Screen.Progress.route,
        Screen.Friends.route
    )

    val fromIsMain = from in mainScreenRoutes
    val toIsMain = to in mainScreenRoutes

    // Animate when:
    // 1. Going from main screen to detail screen (drilling down)
    // 2. Going from detail screen to main screen (going back up)
    // 3. Going between detail screens (lateral navigation in details)
    // Don't animate when: going between main screens (tab switching)
    return !(fromIsMain && toIsMain)
}

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
    // Define which routes should show the navigation bar
    val mainScreenRoutes = setOf(
        Screen.Home.route,
        Screen.Routines.route,
        Screen.Progress.route,
        Screen.Friends.route
    )

    val navigationItems = listOf(Screen.Home, Screen.Routines, Screen.Progress, Screen.Friends)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val previousRoute = remember { mutableStateOf<String?>(null) }

    // Check if current screen should show navigation
    val shouldShowNavigation = currentRoute in mainScreenRoutes

    // Track navigation visibility with smart timing
    var navigationVisible by remember { mutableStateOf(shouldShowNavigation) }

    LaunchedEffect(currentRoute) {
        val wasMainScreen = previousRoute.value in mainScreenRoutes
        val isMainScreen = currentRoute in mainScreenRoutes

        when {
            // Going FROM main TO detail: hide immediately
            wasMainScreen && !isMainScreen -> {
                navigationVisible = false
            }
            // Going FROM detail TO main: show immediately (the animation handles the smooth appearance)
            !wasMainScreen && isMainScreen -> {
                navigationVisible = true
            }
            // Switching between main screens: keep showing
            wasMainScreen && isMainScreen -> {
                navigationVisible = true
            }
            // Switching between detail screens: keep hidden
            !wasMainScreen && !isMainScreen -> {
                navigationVisible = false
            }
        }

        // Update previous route for next navigation
        previousRoute.value = currentRoute
    }

    val isCompact = windowSizeClass == WindowWidthSizeClass.Compact

    if (isCompact) {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                // Navigation bar visibility is now properly synced with screen transitions
                if (navigationVisible) {
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
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        Row(Modifier.fillMaxSize()) {
            // Same logic for navigation rail on larger screens
            if (navigationVisible) {
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
            }
            AppNavHost(
                navController = navController,
                modifier = if (navigationVisible) Modifier.weight(1f) else Modifier.fillMaxSize()
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
        modifier = modifier,
        // Smart animations based on navigation type
        enterTransition = {
            when {
                // Hierarchical navigation (going deeper) - slide from right
                isHierarchicalNavigation(initialState.destination.route, targetState.destination.route) -> {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(350)
                    )
                }
                // Same-level navigation (bottom tabs) - no animation
                else -> EnterTransition.None
            }
        },
        exitTransition = {
            when {
                // Hierarchical navigation (going deeper) - slide to left
                isHierarchicalNavigation(initialState.destination.route, targetState.destination.route) -> {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(350)
                    )
                }
                // Same-level navigation (bottom tabs) - no animation
                else -> ExitTransition.None
            }
        },
        popEnterTransition = {
            when {
                // Coming back in hierarchy - slide from left
                isHierarchicalNavigation(targetState.destination.route, initialState.destination.route) -> {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(350)
                    )
                }
                // Same-level navigation - no animation
                else -> EnterTransition.None
            }
        },
        popExitTransition = {
            when {
                // Coming back in hierarchy - slide to right
                isHierarchicalNavigation(targetState.destination.route, initialState.destination.route) -> {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(350)
                    )
                }
                // Same-level navigation - no animation
                else -> ExitTransition.None
            }
        }
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(
            route = Screen.Exercises.route,
            arguments = listOf(navArgument("initialIds") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
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