package br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui.components.ExerciseTopAppBar
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui.components.ExercisesList

const val SELECTED_EXERCISES_KEY = "selected_exercises"

@Composable
fun ExercisesScreen(
    navController: NavController,
    viewModel: ExercisesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ExerciseTopAppBar(
                searchQuery = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onFilterClick = { /* TODO */ },
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SELECTED_EXERCISES_KEY, uiState.selectedExerciseIds.toList())
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = uiState.selectedExerciseIds.isNotEmpty()
            ) {
                Text("Confirm Selection")
            }
        }
    ) { innerPadding ->
        ExercisesList(
            modifier = Modifier.padding(innerPadding),
            groupedExercises = uiState.groupedExercises,
            selectedExerciseIds = uiState.selectedExerciseIds,
            onExerciseClick = viewModel::toggleExerciseSelection
        )
    }
}