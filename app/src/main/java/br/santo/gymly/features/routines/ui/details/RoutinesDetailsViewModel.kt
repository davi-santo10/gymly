package br.santo.gymly.features.routines.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutineExercise
import br.santo.gymly.features.routines.data.RoutineExerciseCrossRef
import br.santo.gymly.features.routines.data.RoutinesRepository
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Corrected the UiState data class
data class RoutinesDetailsUiState(
    val routine: Routine? = null,
    val routineExercises: List<RoutineExercise> = emptyList(),
    val isEditing: Boolean = false
)

class RoutineDetailsViewModel(
    private val routinesRepository: RoutinesRepository,
    private val exerciseRepository: ExerciseRepository, // This was missing
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutinesDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val routineId: Int = checkNotNull(savedStateHandle["routineId"])

    init {
        viewModelScope.launch {
            val routineFlow = routinesRepository.getRoutineWithExercises(routineId)
            val crossRefsFlow = routinesRepository.getCrossRefsForRoutine(routineId)

            routineFlow.combine(crossRefsFlow) { routineWithExercises, crossRefs ->
                if (routineWithExercises == null) {
                    RoutinesDetailsUiState()
                } else {
                    val crossRefMap = crossRefs.associateBy { it.exerciseId }

                    val detailedExercises = routineWithExercises.exercises.map { exercise ->
                        val crossRef = crossRefMap[exercise.id]
                        RoutineExercise(
                            exercise = exercise,
                            sets = crossRef?.sets ?: "3",
                            reps = crossRef?.reps ?: "10"
                        )
                    }

                    RoutinesDetailsUiState(
                        routine = routineWithExercises.routine,
                        routineExercises = detailedExercises,
                        isEditing = _uiState.value.isEditing
                    )
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }

        val selectedIdsHandle = savedStateHandle.getLiveData<Array<String>>("selected_exercises")
        selectedIdsHandle.observeForever { selectedIds ->
            if (selectedIds != null && selectedIds.isNotEmpty()) {
                viewModelScope.launch {

                    val newExercises = exerciseRepository.getExercisesByIds(selectedIds.toList()).first()
                    val currentExerciseIds = _uiState.value.routineExercises.map {it.exercise.id}
                    val trulyNewExercises = newExercises.filter { it.id !in currentExerciseIds}
                    val newRoutineExercises = trulyNewExercises.map { RoutineExercise(it)}

                    _uiState.update { currentState ->
                        currentState.copy(routineExercises = currentState.routineExercises + newRoutineExercises)
                    }
                    savedStateHandle["selected_exercises"] = null
                }
            }
        }
    }


    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
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



    fun saveChanges() {
        viewModelScope.launch {
            val exercises = _uiState.value.routineExercises

            val crossRefs = exercises.map { routineExercise ->
                RoutineExerciseCrossRef(
                    routineId = routineId,
                    exerciseId = routineExercise.exercise.id,
                    sets = routineExercise.sets,
                    reps = routineExercise.reps
                )
            }

            routinesRepository.deleteCrossRefsForRoutine(routineId)
            routinesRepository.upsertRoutineExerciseCrossRefs(crossRefs)

            toggleEditMode()
        }
    }
}
