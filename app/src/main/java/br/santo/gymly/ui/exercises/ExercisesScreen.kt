package br.santo.gymly.ui.exercises

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.santo.gymly.ExerciseApplication


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExercisesScreen() {
    // Get a reference to our Application class to access the repository
    val application = LocalContext.current.applicationContext as ExerciseApplication

    // Create an instance of our ViewModel using the factory we made.
    // The viewModel() composable is smart and will keep this ViewModel alive
    // during screen rotations.
    val viewModel: ExercisesViewModel = viewModel(
        factory = ExercisesViewModelFactory(application.exerciseRepository)
    )

    // Collect the UI state from the ViewModel.
    // `collectAsStateWithLifecycle` is the recommended, lifecycle-aware way to do this.
    // The `uiState` variable will always hold the latest value from the StateFlow.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // The main UI for the screen
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Loop through each entry in our map of grouped exercises
        uiState.groupedExercises.forEach { (muscleGroup, exercisesInGroup) ->

            // Create a STICKY HEADER for the muscle group name.
            // This header will stick to the top of the screen as you scroll past it.
            stickyHeader {
                MuscleGroupHeader(name = muscleGroup.name)
            }

            // Create a list of rows for all the exercises in this group.
            items(exercisesInGroup) { exercise ->
                ExerciseRow(title = exercise.name)
            }
        }
    }
}

// A simple Composable for the sticky header
@Composable
fun MuscleGroupHeader(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name.replaceFirstChar { it.uppercase() }, // Capitalize the first letter
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant) // Give it a background color
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// A simple Composable for an individual exercise row
@Composable
fun ExerciseRow(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}