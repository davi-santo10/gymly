package br.santo.gymly.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Routines : Screen("routines", "Routines", Icons.AutoMirrored.Filled.List)

    object Exercises : Screen("exercises?initialIds={initialIds}", "Exercises", Icons.Default.FitnessCenter) {
        fun createRoute(initialIds: Set<String>): String {
            val ids = initialIds.joinToString(",")
            return if (ids.isEmpty()) "exercises" else "exercises?initialIds=$ids"
        }
    }

    object Progress : Screen("progress", "Progress", Icons.Default.TrackChanges)
    object Friends : Screen("friends", "Friends", Icons.Default.People)
    object CreateRoutine : Screen("create_routine", "Create Routine", Icons.Default.Add)

    object RoutineDetails : Screen("routine_details/{routineId}", "Routine Details", Icons.Default.Menu) {
        fun createRoute(routineId : Int) : String {
            return "routine_details/$routineId"
        }
    }
    object ActiveWorkout : Screen("active_workout/{routineId}", "Active Workout`", Icons.Default.PlayArrow) {
        fun createRoute(routineId: Int): String {
            return "active_workout/$routineId"
        }
    }

}