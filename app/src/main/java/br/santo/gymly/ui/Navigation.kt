package br.santo.gymly.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Routines : Screen("routines", "Routines", Icons.AutoMirrored.Filled.List)
    object Exercises : Screen("exercises", "Exercises", Icons.Default.FitnessCenter)
    object Progress : Screen("progress", "Progress", Icons.Default.TrackChanges)
    object Friends : Screen("friends", "Friends", Icons.Default.People)
}