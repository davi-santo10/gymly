package br.santo.gymly.features.routines.ui.createroutine

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.santo.gymly.ExerciseApplication
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui.SELECTED_EXERCISES_KEY
import br.santo.gymly.ui.Screen

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun CreateRoutineScreen(navController: NavController) {
    val application = LocalContext.current.applicationContext as ExerciseApplication
    val viewModel: CreateRoutineViewModel = viewModel(
        factory = CreateRoutineViewModelFactory(
            application.routinesRepository,
            application.exerciseRepository
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val routineListResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<List<String>>(SELECTED_EXERCISES_KEY)

    LaunchedEffect(routineListResult) {
        routineListResult?.observeForever { result ->
            viewModel.updateSelectedExercises(result)
            navController.currentBackStackEntry?.savedStateHandle?.remove<List<String>>(
                SELECTED_EXERCISES_KEY
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Create new routine")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()}) {
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
                        label = { Text("Routine Name")},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                items(uiState.routineExercises, key = { it.exercise.id}) { routineExercise ->
                    RoutineExerciseItem(
                        routineExercise = routineExercise,
                        onSetsChanged = { newSets ->
                            viewModel.onSetsChanged(routineExercise.exercise.id, newSets)
                        },
                        onRepsChanged = { newReps ->
                            viewModel.onRepsChanged(routineExercise.exercise.id, newReps)
                        },
                        isEditable = true
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
                    .padding(16.dp)
            ) {
                Text("Save Routine")
            }

        }
    }
}