package br.santo.gymly.features.routines.activeworkout.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.activeworkout.data.ActiveWorkout
import br.santo.gymly.features.routines.activeworkout.data.ActiveWorkoutExercise
import br.santo.gymly.features.routines.activeworkout.data.WorkoutSet
import br.santo.gymly.features.routines.data.RoutinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveWorkoutUiState (
    val activeWorkout: ActiveWorkout? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor (
    private val routinesRepository: RoutinesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val routineId: Int = checkNotNull(savedStateHandle["routineId"])

    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadRoutineAndStartWorkout()
    }

    private fun loadRoutineAndStartWorkout() {
        viewModelScope.launch {
            try {
                routinesRepository.getRoutineWithExercises(routineId).collect { routineWithExercises ->
                    if (routineWithExercises != null ) {
                        val activeExercises = routineWithExercises.exercises.map { exercise ->
                            val crossRefs = routinesRepository.getCrossRefsForRoutine(routineId)
                            var exerciseConfig: br.santo.gymly.features.routines.data.RoutineExerciseCrossRef? = null

                            crossRefs.collect { refs ->
                                exerciseConfig = refs.find { it.exerciseId == exercise.id}
                            }

                            val targetSets = exerciseConfig?.sets?.toIntOrNull() ?: 3
                            val targetReps = exerciseConfig?.reps?.toIntOrNull() ?: 10
                            val restTime = exerciseConfig?.restTimeSeconds ?: 60

                            val workoutSets = (1..targetSets).map { setNumber ->
                                WorkoutSet(
                                    setNumber = setNumber,
                                    targetReps = targetReps,
                                    actualReps = 0,
                                    weight = 0f,
                                    isCompleted = false
                                )
                            }

                            ActiveWorkoutExercise(
                                exercise = exercise,
                                sets = workoutSets,
                                isExpanded = false,
                                targetSets = targetSets,
                                restTimeSeconds = restTime
                            )
                        }

                        val activeWorkout = ActiveWorkout(
                            routineId = routineId,
                            routineName = routineWithExercises.routine.name,
                            exercises = activeExercises,
                            startTime = System.currentTimeMillis(),
                            isFinished = false
                        )

                        _uiState.update {
                            it.copy(
                                activeWorkout = activeWorkout,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy (
                        isLoading = false,
                        error = "Failed to load workout: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleExerciseExpansion(exerciseIndex: Int) {
        _uiState.update { currentState ->
            val currentWorkout = currentState.activeWorkout ?: return@update currentState

            val updatedExercises = currentWorkout.exercises.mapIndexed { index, exercise ->
                if (index == exerciseIndex) {
                    exercise.copy(isExpanded = !exercise.isExpanded)
                } else {
                    exercise
                }
            }
            currentState.copy (
                activeWorkout = currentWorkout.copy(exercises = updatedExercises)
            )
        }
    }

    fun updateSetReps (exerciseIndex: Int, setIndex: Int, reps: Int) {
        updateWorkoutSet(exerciseIndex, setIndex) { set ->
            set.copy(actualReps = reps)
        }
    }

    fun updateSetWeight (exerciseIndex: Int, setIndex: Int, weight: Float) {
        updateWorkoutSet(exerciseIndex, setIndex) { set ->
            set.copy(weight = weight)
        }
    }

    fun toggleSetCompletion(exerciseIndex: Int, setIndex: Int) {
        updateWorkoutSet(exerciseIndex,setIndex)  { set ->
            set.copy(isCompleted = !set.isCompleted)
        }
    }

    private fun updateWorkoutSet(
        exerciseIndex: Int,
        setIndex: Int,
        updatedFunction: (WorkoutSet) -> WorkoutSet
    ) {
        _uiState.update { currentState ->
            val currentWorkout = currentState.activeWorkout ?: return@update currentState

            val updatedExercises = currentWorkout.exercises.mapIndexed { eIndex, exercise ->
                if (eIndex == exerciseIndex) {
                    val updatedSets = exercise.sets.mapIndexed { sIndex, set ->
                        if (sIndex == setIndex ) updatedFunction(set) else set
                    }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
            currentState.copy(
                activeWorkout = currentWorkout.copy(exercises = updatedExercises)
            )
        }
    }

    fun finishWorkout (onWorkoutFinished: () -> Unit) {
        _uiState.update { currentState ->
            val currentWorkout = currentState.activeWorkout ?: return@update currentState

            val finishedWorkout = currentWorkout.copy(isFinished = true)

            onWorkoutFinished()

            currentState.copy(activeWorkout = finishedWorkout)
        }
    }
}