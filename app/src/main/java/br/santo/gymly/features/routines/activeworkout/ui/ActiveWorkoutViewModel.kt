package br.santo.gymly.features.routines.activeworkout.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.santo.gymly.features.routines.activeworkout.data.ActiveWorkout
import br.santo.gymly.features.routines.activeworkout.data.ActiveWorkoutExercise
import br.santo.gymly.features.routines.activeworkout.data.WorkoutSet
import br.santo.gymly.features.routines.data.RoutinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestTimerState(
    val exerciseIndex: Int,
    val exerciseName: String,
    val totalRestTimeSeconds: Int,
    val remainingTimeSeconds: Int,
    val isRunning: Boolean,
    val startTimestamp: Long
)
data class ActiveWorkoutUiState (
    val activeWorkout: ActiveWorkout? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val expandedExerciseIndex: Int? = null,
    val restTimer: RestTimerState? = null
)

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor (
    private val routinesRepository: RoutinesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val routineId: Int = checkNotNull(savedStateHandle["routineId"])

    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadRoutineAndStartWorkout()
    }

    private fun loadRoutineAndStartWorkout() {
        viewModelScope.launch {
            try {
                val routineFlow = routinesRepository.getRoutineWithExercises(routineId)
                val crossRefsFlow = routinesRepository.getCrossRefsForRoutine(routineId)

                routineFlow.combine(crossRefsFlow) { routineWithExercises, crossRefs ->
                    if (routineWithExercises == null) {
                        null
                    } else {
                        val crossRefMap = crossRefs.associateBy { it.exerciseId }

                        val activeExercises = routineWithExercises.exercises.map { exercise ->
                            val exerciseConfig = crossRefMap[exercise.id]

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
                                targetSets = targetSets,
                                restTimeSeconds = restTime
                            )
                        }

                        ActiveWorkout(
                            routineId = routineId,
                            routineName = routineWithExercises.routine.name,
                            exercises = activeExercises,
                            startTime = System.currentTimeMillis(),
                            isFinished = false
                        )
                    }
                }.collect { activeWorkout ->
                    if (activeWorkout == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Routine not found"
                            )
                        }
                    } else {
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
            val currentExpandedIndex = currentState.expandedExerciseIndex

            val newExpandedIndex = if (currentExpandedIndex == exerciseIndex) {
                null
            } else {
                exerciseIndex
            }
            currentState.copy(expandedExerciseIndex = newExpandedIndex)
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
            val newCompletionState = !set.isCompleted

            if (newCompletionState) {
                startRestTimer(exerciseIndex)
            }

            set.copy(isCompleted = newCompletionState)
        }
    }

    private fun startRestTimer(exerciseIndex: Int) {
        val currentWorkout = _uiState.value.activeWorkout ?: return
        val exercise = currentWorkout.exercises.getOrNull(exerciseIndex) ?: return

        stopRestTimer()

        val restTimeSeconds = exercise.restTimeSeconds
        val currentTimestamp = System.currentTimeMillis()

        val timerState = RestTimerState(
            exerciseIndex = exerciseIndex,
            exerciseName = exercise.exercise.name,
            totalRestTimeSeconds = restTimeSeconds,
            remainingTimeSeconds = restTimeSeconds,
            isRunning = true,
            startTimestamp = currentTimestamp
        )

        _uiState.update { it.copy(restTimer = timerState)}

        timerJob = viewModelScope.launch {
            var remainingSeconds = restTimeSeconds

            while (remainingSeconds > 0 && _uiState.value.restTimer?.isRunning == true) {
                delay(1000)
                remainingSeconds--

                _uiState.update {currentState ->
                    currentState.restTimer?.let { timer ->
                        currentState.copy(
                            restTimer = timer.copy(remainingTimeSeconds = remainingSeconds)
                        )
                    } ?: currentState
                }
            }

            if (remainingSeconds <= 0) {
                onTimerFinished()
            }
        }
    }

    private fun onTimerFinished() {
        _uiState.update { currentState ->
            currentState.copy (
                restTimer = currentState.restTimer?.copy(
                    isRunning = false,
                    remainingTimeSeconds = 0
                )
            )
        }

        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(restTimer = null)}
        }
    }

    fun stopRestTimer() {
        timerJob?.cancel ()
        timerJob = null
        _uiState.update { it.copy(restTimer = null)}
    }

    fun toggleTimerPause() {
        _uiState.update { currentState ->
            currentState.restTimer?.let { timer ->
                if (timer.isRunning) {
                    timerJob?.cancel()
                    currentState.copy(restTimer = timer.copy(isRunning = false))
                } else {
                    startRestTimerWithRemaining(timer.exerciseIndex, timer.remainingTimeSeconds)
                    currentState
                }
            } ?: currentState
        }
    }

    private fun startRestTimerWithRemaining(exerciseIndex: Int, remainingSeconds: Int) {
        val currentWorkout = _uiState.value.activeWorkout ?: return
        val exercise = currentWorkout.exercises.getOrNull(exerciseIndex) ?: return

        timerJob?.cancel()

        _uiState.update { currentState ->
            currentState.copy (
                restTimer = currentState.restTimer?.copy(isRunning = true)
            )
        }

        timerJob = viewModelScope.launch {
            var timeLeft = remainingSeconds

            while (timeLeft > 0 && _uiState.value.restTimer?.isRunning == true) {
                delay (1000)
                timeLeft--

                _uiState.update { currentState ->
                    currentState.restTimer?.let { timer ->
                        currentState.copy(
                            restTimer = timer.copy(remainingTimeSeconds = timeLeft)
                        )
                    } ?: currentState
                }
            }

            if (timeLeft <= 0) {
                onTimerFinished()
            }
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
        stopRestTimer()

        _uiState.update { currentState ->
            val currentWorkout = currentState.activeWorkout ?: return@update currentState

            val finishedWorkout = currentWorkout.copy(isFinished = true)

            onWorkoutFinished()

            currentState.copy(activeWorkout = finishedWorkout)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopRestTimer()
    }
}