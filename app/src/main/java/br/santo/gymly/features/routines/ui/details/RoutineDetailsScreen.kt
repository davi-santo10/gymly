package br.santo.gymly.features.routines.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.santo.gymly.features.routines.ui.createroutine.RoutineExerciseItem
import br.santo.gymly.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailsScreen(
    navController: NavController,
    routineId: Int,
) {
    val viewModel: RoutineDetailsViewModel = viewModel(factory = RoutineDetailsViewModelFactory.Factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val routine = uiState.routine
    val routineExercises = uiState.routineExercises

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = routine?.name ?: "Loading") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        // FIXED: Changed viewModel::saveChanges to viewModel.saveChanges()
                        IconButton(onClick = { viewModel.saveChanges() }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                        // FIXED: Changed viewModel::toggleEditMode to viewModel.toggleEditMode()
                        IconButton(onClick = { viewModel.toggleEditMode() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel Changes")
                        }
                    } else {
                        // FIXED: Changed viewModel::toggleEditMode to viewModel.toggleEditMode()
                        IconButton(onClick = { viewModel.toggleEditMode() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Routine")
                        }
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { innerPadding ->
        if (routine != null) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Text("Exercises", style = MaterialTheme.typography.titleLarge)
                }

                itemsIndexed(routineExercises, key = { _, item -> item.exercise.id }) { index, routineExercise ->
                    RoutineExerciseItem(
                        routineExercise = routineExercise,
                        onSetsChanged = { newSets ->
                            viewModel.onSetsChanged(routineExercise.exercise.id, newSets)
                        },
                        onRepsChanged = { newReps ->
                            viewModel.onRepsChanged(routineExercise.exercise.id, newReps)
                        },
                        isEditable = uiState.isEditing // MODIFIED: Make this depend on the edit state
                    )
                }

                if (uiState.isEditing) {
                    item {
                        Button(
                            onClick = {
                                val currentIds =
                                    uiState.routineExercises.map { it.exercise.id }.toSet()
                                navController.navigate(Screen.Exercises.createRoute(currentIds))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Exercise")
                        }
                    }
                }
            }
        }
    }
}