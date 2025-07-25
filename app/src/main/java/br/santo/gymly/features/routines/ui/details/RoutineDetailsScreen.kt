package br.santo.gymly.features.routines.ui.details

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import br.santo.gymly.features.routines.ui.components.RoutineExerciseItem
import br.santo.gymly.features.routines.ui.components.TimerSelectionSheet
import br.santo.gymly.ui.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RoutineDetailsScreen(
    navController: NavController,
    viewModel: RoutineDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val routine = uiState.routine

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = routine?.name ?: "Loading") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.isEditing) {
                            viewModel.discardChanges()
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        val icon = if (uiState.isEditing) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack
                        Icon(
                            imageVector = icon,
                            contentDescription = if (uiState.isEditing) "Discard Changes" else "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = { viewModel.saveChanges() }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    } else {
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

                itemsIndexed(
                    uiState.routineExercises,
                    key = { _, item -> item.exercise.id }) { index, routineExercise ->
                    RoutineExerciseItem(
                        modifier = Modifier.animateItemPlacement(),
                        routineExercise = routineExercise,
                        onSetsChanged = { newSets ->
                            viewModel.onSetsChanged(routineExercise.exercise.id, newSets)
                        },
                        onRepsChanged = { newReps ->
                            viewModel.onRepsChanged(routineExercise.exercise.id, newReps)
                        },
                        isEditable = uiState.isEditing,
                        onMoveUp = { viewModel.moveExerciseUp(index) },
                        onMoveDown = { viewModel.moveExerciseDown(index) },
                        isFirst = index == 0,
                        isLast = index == uiState.routineExercises.lastIndex,
                        onTimerClick = { exerciseId ->
                            viewModel.onTimerIconClick(exerciseId)
                        }
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

    if (uiState.isTimerSheetVisible && uiState.exerciseToSetTimer != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onDismissTimerSheet() },
            sheetState = sheetState,
        ) {
            TimerSelectionSheet(
                currentRestTimeSeconds = uiState.exerciseToSetTimer!!.restTimeSeconds,
                onTimeSelected = { seconds ->
                    viewModel.onTimeSelected(seconds)
                }
            )
        }
    }
}