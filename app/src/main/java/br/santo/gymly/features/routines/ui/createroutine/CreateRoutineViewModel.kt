package br.santo.gymly.features.routines.ui.createroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutineExercise
import br.santo.gymly.features.routines.data.RoutineExerciseCrossRef
import br.santo.gymly.features.routines.data.RoutinesRepository
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateRoutineUiState(
    val routineName: String = "",
    val routineExercises: List<RoutineExercise> = emptyList()
)

class CreateRoutineViewModel(
    private val routinesRepository: RoutinesRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRoutineUiState())
    val uiState = _uiState.asStateFlow()

    fun updateRoutineName(newName: String) {
        _uiState.update { it.copy(routineName = newName) }
    }

    // THIS IS THE NEW, CORRECTED FUNCTION
    fun updateSelectedExercises(selectedIds: List<String>) {
        viewModelScope.launch {
            val newExercises = exerciseRepository.getExercisesByIds(selectedIds).first()
            val currentExerciseIds = _uiState.value.routineExercises.map { it.exercise.id }
            val trulyNewExercises = newExercises.filter { it.id !in currentExerciseIds }
            val newRoutineExercises = trulyNewExercises.map { RoutineExercise(it) }

            _uiState.update { currentState ->
                currentState.copy(routineExercises = currentState.routineExercises + newRoutineExercises)
            }
        }
    }

    fun onSetsChanged(exerciseId: String, newSets: String) {
        _uiState.update { currentState ->
            val updatedExercises = currentState.routineExercises.map { routineExercise ->
                if (routineExercise.exercise.id == exerciseId) {
                    routineExercise.copy(sets = newSets)
                } else {
                    routineExercise
                }
            }
            currentState.copy(routineExercises = updatedExercises)
        }
    }

    fun onRepsChanged(exerciseId: String, newReps: String) {
        _uiState.update { currentState ->
            val updatedExercises = currentState.routineExercises.map { routineExercise ->
                if (routineExercise.exercise.id == exerciseId) {
                    routineExercise.copy(reps = newReps)
                } else {
                    routineExercise
                }
            }
            currentState.copy(routineExercises = updatedExercises)
        }
    }

    fun moveExercise(from: Int, to: Int) {
        if (from == to) return
        _uiState.update { currentState ->
            val mutableExercises = currentState.routineExercises.toMutableList()
            val item = mutableExercises.removeAt(from)
            mutableExercises.add(to, item)
            currentState.copy(routineExercises = mutableExercises)
        }
    }

    fun saveRoutine(onRoutineSaved: () -> Unit) {
        viewModelScope.launch {
            val newRoutineId = routinesRepository.upsertRoutine(
                Routine(name = _uiState.value.routineName)
            ).toInt()

            val crossRefs = _uiState.value.routineExercises.map { routineExercise ->
                RoutineExerciseCrossRef(
                    routineId = newRoutineId,
                    exerciseId = routineExercise.exercise.id,
                    sets = routineExercise.sets,
                    reps = routineExercise.reps
                )
            }
            routinesRepository.upsertRoutineExerciseCrossRefs(crossRefs)
            onRoutineSaved()
        }
    }
}