package br.santo.gymly.features.routines.ui.details

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutinesDetailsUiState(
    val routine: Routine? = null,
    val routineExercises: List<RoutineExercise> = emptyList(),
    val isEditing: Boolean = false,
    val isTimerSheetVisible: Boolean = false,
    val exerciseToSetTimer: RoutineExercise? = null
)

@HiltViewModel
class RoutineDetailsViewModel @Inject constructor(
    private val routinesRepository: RoutinesRepository,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutinesDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val routineId: Int = checkNotNull(savedStateHandle["routineId"])
    private var originalExercises: List<RoutineExercise> = emptyList()

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
                            reps = crossRef?.reps ?: "10",
                            order = crossRef?.order ?: 0,
                            restTimeSeconds = crossRef?.restTimeSeconds ?: 60
                        )
                    }

                    RoutinesDetailsUiState(
                        routine = routineWithExercises.routine,
                        routineExercises = detailedExercises.sortedBy { it.order },
                        isEditing = _uiState.value.isEditing
                    )
                }
            }.collect { newState ->
                _uiState.value = newState

                if (originalExercises.isEmpty()) {
                    originalExercises = newState.routineExercises
                }
            }
        }

        val selectedIdsHandle = savedStateHandle.getLiveData<Array<String>>("selected_exercises")
        selectedIdsHandle.observeForever { selectedIds ->
            if (selectedIds != null && selectedIds.isNotEmpty()) {
                viewModelScope.launch {
                    val newExercises = exerciseRepository.getExercisesByIds(selectedIds.toList()).first()
                    val currentExerciseIds = _uiState.value.routineExercises.map { it.exercise.id }
                    val trulyNewExercises = newExercises.filter { it.id !in currentExerciseIds }

                    val lastOrder = _uiState.value.routineExercises.maxOfOrNull { it.order } ?: -1
                    val newRoutineExercises = trulyNewExercises.mapIndexed { index, exercise ->
                        RoutineExercise(
                            exercise = exercise,
                            order = lastOrder + 1 + index
                        )
                    }

                    _uiState.update { currentState ->
                        currentState.copy(routineExercises = currentState.routineExercises + newRoutineExercises)
                    }
                    savedStateHandle["selected_exercises"] = null
                }
            }
        }
    }

    fun toggleEditMode() {
        if (_uiState.value.isEditing) {
            discardChanges()
        } else {
            _uiState.update { it.copy(isEditing = true) }
            originalExercises = _uiState.value.routineExercises
        }
    }

    fun discardChanges() {
        _uiState.update {
            it.copy(
                routineExercises = originalExercises,
                isEditing = false
            )
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
        if (!_uiState.value.isEditing) return
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

    fun saveChanges() {
        viewModelScope.launch {
            val exercises = _uiState.value.routineExercises
            val crossRefs = exercises.mapIndexed { index, routineExercise ->
                RoutineExerciseCrossRef(
                    routineId = routineId,
                    exerciseId = routineExercise.exercise.id,
                    sets = routineExercise.sets,
                    reps = routineExercise.reps,
                    order = index,
                    restTimeSeconds = routineExercise.restTimeSeconds
                )
            }
            routinesRepository.deleteCrossRefsForRoutine(routineId)
            routinesRepository.upsertRoutineExerciseCrossRefs(crossRefs)
            toggleEditMode()
        }
    }
}
