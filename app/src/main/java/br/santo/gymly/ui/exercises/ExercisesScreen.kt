package br.santo.gymly.ui.exercises

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.santo.gymly.ExerciseApplication
import br.santo.gymly.ui.exercises.components.ExerciseTopAppBar
import br.santo.gymly.ui.exercises.components.ExercisesList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    modifier: Modifier = Modifier,
) {
    val application = LocalContext.current.applicationContext as ExerciseApplication
    val viewModel: ExercisesViewModel = viewModel(
        factory = ExercisesViewModelFactory(application.exerciseRepository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO */ },
                icon = { Icon(Icons.Default.Add, "Add Exercise") },
                text = { Text("Add Exercise") }
            )
        },


    ) {
        Column {
            ExerciseTopAppBar(
                searchQuery = searchQuery,
                onQueryChange = { newQuery ->
                    searchQuery = newQuery
                    viewModel.onSearchQueryChanged(newQuery)
                },
                onFilterClick = { /* TODO */ },
                scrollBehavior = scrollBehavior
            )
            ExercisesList(
                groupedExercises = uiState.groupedExercises,
                listState = listState
            )
        }
    }
}