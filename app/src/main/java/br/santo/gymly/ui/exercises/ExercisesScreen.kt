package br.santo.gymly.ui.exercises

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.santo.gymly.ExerciseApplication
import br.santo.gymly.ui.exercises.components.ExercisesList

@Composable
fun ExercisesScreen (
    modifier : Modifier = Modifier,
) {
    val application = LocalContext.current.applicationContext as ExerciseApplication
    val viewModel: ExercisesViewModel = viewModel(
        factory = ExercisesViewModelFactory(application.exerciseRepository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // The Scaffold provides the overall structure and bottom padding.
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO */ },
                icon = { Icon(Icons.Default.Add, "Add Exercise") },
                text = { Text("Add Exercise") }
            )
        }
    ) { innerPadding ->
        // The ExercisesList is the only content.
        // It receives all the necessary state and the padding from the Scaffold.
        ExercisesList(
            groupedExercises = uiState.groupedExercises,
            listState = listState,
            searchQuery = searchQuery,
            onQueryChange = { newQuery ->
                searchQuery = newQuery
                viewModel.onSearchQueryChanged(newQuery)
            },
            onFilterClick = { /* TODO */ },
            contentPadding = innerPadding
        )
    }
}
