package br.santo.gymly.features.routines.ui.createroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutineExercise
import br.santo.gymly.features.routines.data.RoutineExerciseCrossRef
import br.santo.gymly.features.routines.data.RoutinesRepository
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateRoutineUiState(
    val routineName: String = "",
    val routineExercises: List<RoutineExercise> = emptyList(),
    val isTimerSheetVisible: Boolean = false,
    val exerciseToSetTimer: RoutineExercise? = null
)

@HiltViewModel
class CreateRoutineViewModel @Inject constructor (
    private val routinesRepository: RoutinesRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRoutineUiState())
    val uiState = _uiState.asStateFlow()

    fun updateRoutineName(newName: String) {
        _uiState.update { it.copy(routineName = newName) }
    }

    fun updateSelectedExercises(selectedIds: List<String>) {
        viewModelScope.launch {
            val newExercises = exerciseRepository.getExercisesByIds(selectedIds).first()
            val currentExercises = _uiState.value.routineExercises
            val currentExerciseIds = currentExercises.map { it.exercise.id }
            val trulyNewExercises = newExercises.filter { it.id !in currentExerciseIds }

            val lastOrder = currentExercises.maxOfOrNull { it.order } ?: -1
            val newRoutineExercises = trulyNewExercises.mapIndexed { index, exercise ->
                RoutineExercise(
                    exercise = exercise,
                    order = lastOrder + 1 + index
                )
            }

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

    fun moveExerciseUp(index: Int) {
        if (index > 0) {
            val exercises = _uiState.value.routineExercises.toMutableList()
            val exercise = exercises.removeAt(index)
            exercises.add(index - 1, exercise)
            _uiState.update { it.copy(routineExercises = exercises) }
        }
    }

    fun moveExerciseDown(index: Int) {
        val currentExercises = _uiState.value.routineExercises
        if (index < currentExercises.size - 1) {
            val exercises = currentExercises.toMutableList()
            val exercise = exercises.removeAt(index)
            exercises.add(index + 1, exercise)
            _uiState.update { it.copy(routineExercises = exercises) }
        }
    }

    fun onTimerIconClick(exerciseId: String) {
        val exercise = _uiState.value.routineExercises.find { it.exercise.id == exerciseId }
        _uiState.update {
            it.copy(
                isTimerSheetVisible = true,
                exerciseToSetTimer = exercise
            )
        }
    }

    fun onDismissTimerSheet() {
        _uiState.update {
            it.copy(
                isTimerSheetVisible = false,
                exerciseToSetTimer = null
            )
        }
    }

    fun onTimeSelected(seconds: Int) {
        val exerciseId = _uiState.value.exerciseToSetTimer?.exercise?.id ?: return

        _uiState.update { currentState ->
            val updatedExercises = currentState.routineExercises.map {
                if (it.exercise.id == exerciseId) {
                    it.copy(restTimeSeconds = seconds)
                } else {
                    it
                }
            }
            currentState.copy(
                routineExercises = updatedExercises,
                isTimerSheetVisible = false,
                exerciseToSetTimer = null
            )
        }
    }

    fun saveRoutine(onRoutineSaved: () -> Unit) {
        viewModelScope.launch {
            // Step 1: Save the routine and get its new ID
            val newRoutineId = routinesRepository.upsertRoutine(
                Routine(name = _uiState.value.routineName)
            ).toInt()

            // Step 2: If there are exercises, create the links (CrossRefs) and save them
            if (_uiState.value.routineExercises.isNotEmpty()) {
                val crossRefs = _uiState.value.routineExercises.mapIndexed { index, routineExercise ->
                    RoutineExerciseCrossRef(
                        routineId = newRoutineId,
                        exerciseId = routineExercise.exercise.id,
                        sets = routineExercise.sets,
                        reps = routineExercise.reps,
                        order = index,
                        restTimeSeconds = routineExercise.restTimeSeconds
                    )
                }
                // Use the correct repository method to save the links
                routinesRepository.upsertRoutineExerciseCrossRefs(crossRefs)
            }

            // Step 3: Navigate back
            onRoutineSaved()
        }
    }
}