package br.santo.gymly.features.routines.ui.createroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.exercises.data.ExerciseRepository
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutineExercise
import br.santo.gymly.features.routines.data.RoutinesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateRoutineUiState(
    val routineName: String = "",
    val routineDescription: String = "",
    val routineExercises: List<RoutineExercise> = emptyList()
)


class CreateRoutineViewModel(
    private val routinesRepository: RoutinesRepository,
    private val exerciseRepository: ExerciseRepository
    ) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRoutineUiState())

    val uiState = _uiState.asStateFlow()

    fun updateRoutineName(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(routineName = newName)
        }
    }

    fun updateRoutineDescription(newDescription: String) {
        _uiState.update { currentState ->
            currentState.copy(routineDescription = newDescription)
        }
    }

    fun updateSelectedExercises(exerciseIds: List<String>) {
       viewModelScope.launch {
           val allExercises = exerciseRepository.allExercises.first()
           val selectedExercises = allExercises
               .filter { it.id in exerciseIds}
               .map { RoutineExercise(exercise = it) }

           _uiState.update { it.copy(routineExercises = selectedExercises)}
        }
    }

    fun onSetsChanged(exerciseId: String, newSets: String) {
        _uiState.update { currentState ->
            val updatedList = currentState.routineExercises.map {
                if (it.exercise.id == exerciseId) it.copy(sets = newSets) else it
            }
            currentState.copy(routineExercises = updatedList)
        }
    }

    fun onRepsChanged(exerciseId: String, newReps: String) {
        _uiState.update { currentState ->
            val updatedList = currentState.routineExercises.map {
                if (it.exercise.id == exerciseId) it.copy(reps = newReps) else it
            }
            currentState.copy(routineExercises = updatedList)
        }
    }

    fun saveRoutine(onRoutineSaved: () -> Unit) {
        if (uiState.value.routineName.isBlank()) {
            return
        }

        viewModelScope.launch {
            val newRoutine = Routine(
               name = uiState.value.routineName,
                description = uiState.value.routineDescription
            )
            routinesRepository.upsertRoutine(newRoutine)
            onRoutineSaved()
        }
    }
}