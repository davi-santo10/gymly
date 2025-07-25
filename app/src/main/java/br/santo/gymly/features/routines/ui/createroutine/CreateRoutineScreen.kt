package br.santo.gymly.features.routines.ui.createroutine

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import br.santo.gymly.features.routines.ui.components.RoutineExerciseItem
import br.santo.gymly.features.routines.ui.components.TimerSelectionSheet
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui.SELECTED_EXERCISES_KEY
import br.santo.gymly.ui.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateRoutineScreen(
    navController: NavController,
    viewModel: CreateRoutineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val routineListResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<List<String>>(SELECTED_EXERCISES_KEY)

    LaunchedEffect(routineListResult) {
        routineListResult?.observeForever { result ->
            if (result != null) {
                viewModel.updateSelectedExercises(result)
                navController.currentBackStackEntry?.savedStateHandle?.remove<List<String>>(
                    SELECTED_EXERCISES_KEY
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Create new routine")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = uiState.routineName,
                        onValueChange = viewModel::updateRoutineName,
                        label = { Text("Routine Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                itemsIndexed(uiState.routineExercises, key = { _, it -> it.exercise.id }) { index, routineExercise ->
                    RoutineExerciseItem(
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    visibilityThreshold = IntOffset.VisibilityThreshold
                                )
                        ),
                        routineExercise = routineExercise,
                        onSetsChanged = { newSets ->
                            viewModel.onSetsChanged(routineExercise.exercise.id, newSets)
                        },
                        onRepsChanged = { newReps ->
                            viewModel.onRepsChanged(routineExercise.exercise.id, newReps)
                        },
                        isEditable = true,
                        onMoveUp = { viewModel.moveExerciseUp(index) },
                        onMoveDown = { viewModel.moveExerciseDown(index) },
                        isFirst = index == 0,
                        isLast = index == uiState.routineExercises.lastIndex,
                        onTimerClick = { exerciseId ->
                            viewModel.onTimerIconClick(exerciseId)
                        }
                    )
                }
                item {
                    OutlinedButton(
                        onClick = {
                            val currentIds = uiState.routineExercises.map { it.exercise.id }.toSet()
                            navController.navigate(Screen.Exercises.createRoute(currentIds))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add exercises")
                    }
                }
            }
            Button(
                onClick = {
                    viewModel.saveRoutine {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = uiState.routineName.isNotBlank() && uiState.routineExercises.isNotEmpty()
            ) {
                Text("Save Routine")
            }
        }
    }

    if (uiState.isTimerSheetVisible && uiState.exerciseToSetTimer != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onDismissTimerSheet() },
            sheetState = sheetState
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