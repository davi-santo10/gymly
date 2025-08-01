package br.santo.gymly.features.routines.ui.createroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.data.ExerciseGroup
import br.santo.gymly.features.routines.data.GroupType
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutineExercise
import br.santo.gymly.features.routines.data.RoutineExerciseCrossRef
import br.santo.gymly.features.routines.data.RoutineExerciseGroup
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
    val individualExercises: List<RoutineExercise> = emptyList(), // Ungrouped exercises
    val exerciseGroups: List<RoutineExerciseGroup> = emptyList(), // Exercise groups
    val isTimerSheetVisible: Boolean = false,
    val exerciseToSetTimer: RoutineExercise? = null,
    val isGroupCreationDialogVisible: Boolean = false,
    val isGroupEditDialogVisible: Boolean = false,
    val groupBeingEdited: RoutineExerciseGroup? = null
) {
    /**
     * All exercises available for grouping (currently individual exercises)
     */
    val availableExercisesForGrouping: List<RoutineExercise>
        get() = individualExercises

    /**
     * Total count of exercises (individual + grouped)
     */
    val totalExerciseCount: Int
        get() = individualExercises.size + exerciseGroups.sumOf { it.exercises.size }

    /**
     * Check if routine is valid for saving
     */
    val isValidForSaving: Boolean
        get() = routineName.isNotBlank() && totalExerciseCount > 0
}

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
            val currentExercises = _uiState.value.individualExercises
            val currentExerciseIds = currentExercises.map { it.exercise.id }
            val trulyNewExercises = newExercises.filter { it.id !in currentExerciseIds }

            val lastOrder = getNextExerciseOrder()
            val newRoutineExercises = trulyNewExercises.mapIndexed { index, exercise ->
                RoutineExercise(
                    exercise = exercise,
                    order = lastOrder + index
                )
            }

            _uiState.update { currentState ->
                currentState.copy(individualExercises = currentState.individualExercises + newRoutineExercises)
            }
        }
    }

    // Individual Exercise Management (unchanged from original)
    fun onSetsChanged(exerciseId: String, newSets: String) {
        _uiState.update { currentState ->
            val updatedExercises = currentState.individualExercises.map { routineExercise ->
                if (routineExercise.exercise.id == exerciseId) {
                    routineExercise.copy(sets = newSets)
                } else {
                    routineExercise
                }
            }
            currentState.copy(individualExercises = updatedExercises)
        }
    }

    fun onRepsChanged(exerciseId: String, newReps: String) {
        _uiState.update { currentState ->
            val updatedExercises = currentState.individualExercises.map { routineExercise ->
                if (routineExercise.exercise.id == exerciseId) {
                    routineExercise.copy(reps = newReps)
                } else {
                    routineExercise
                }
            }
            currentState.copy(individualExercises = updatedExercises)
        }
    }

    fun moveIndividualExerciseUp(index: Int) {
        if (index > 0) {
            val exercises = _uiState.value.individualExercises.toMutableList()
            val exercise = exercises.removeAt(index)
            exercises.add(index - 1, exercise)
            _uiState.update { it.copy(individualExercises = exercises) }
        }
    }

    fun moveIndividualExerciseDown(index: Int) {
        val currentExercises = _uiState.value.individualExercises
        if (index < currentExercises.size - 1) {
            val exercises = currentExercises.toMutableList()
            val exercise = exercises.removeAt(index)
            exercises.add(index + 1, exercise)
            _uiState.update { it.copy(individualExercises = exercises) }
        }
    }

    // NEW: Group Management Methods

    fun showGroupCreationDialog() {
        _uiState.update { it.copy(isGroupCreationDialogVisible = true) }
    }

    fun hideGroupCreationDialog() {
        _uiState.update { it.copy(isGroupCreationDialogVisible = false) }
    }

    fun createExerciseGroup(
        name: String,
        selectedExercises: List<RoutineExercise>,
        groupType: GroupType,
        restTimeSeconds: Int
    ) {
        val newGroup = ExerciseGroup(
            id = 0, // Will be assigned by Room
            routineId = 0, // Will be set when saving the routine
            name = name,
            restTimeSeconds = restTimeSeconds,
            order = getNextGroupOrder(),
            type = groupType
        )

        val routineExerciseGroup = RoutineExerciseGroup(
            group = newGroup,
            exercises = selectedExercises.mapIndexed { index, exercise ->
                exercise.copy(order = index) // Order within the group
            }
        )

        _uiState.update { currentState ->
            // Remove selected exercises from individual exercises
            val remainingIndividualExercises = currentState.individualExercises.filter { individual ->
                selectedExercises.none { selected -> selected.exercise.id == individual.exercise.id }
            }

            currentState.copy(
                exerciseGroups = currentState.exerciseGroups + routineExerciseGroup,
                individualExercises = remainingIndividualExercises,
                isGroupCreationDialogVisible = false
            )
        }
    }

    fun showGroupEditDialog(group: RoutineExerciseGroup) {
        _uiState.update {
            it.copy(
                isGroupEditDialogVisible = true,
                groupBeingEdited = group
            )
        }
    }

    fun hideGroupEditDialog() {
        _uiState.update {
            it.copy(
                isGroupEditDialogVisible = false,
                groupBeingEdited = null
            )
        }
    }

    fun updateExerciseGroup(
        originalGroup: RoutineExerciseGroup,
        name: String,
        selectedExercises: List<RoutineExercise>,
        groupType: GroupType,
        restTimeSeconds: Int
    ) {
        val updatedGroup = originalGroup.copy(
            group = originalGroup.group.copy(
                name = name,
                restTimeSeconds = restTimeSeconds,
                type = groupType
            ),
            exercises = selectedExercises.mapIndexed { index, exercise ->
                exercise.copy(order = index)
            }
        )

        _uiState.update { currentState ->
            // Find exercises that were removed from the group
            val originalExerciseIds = originalGroup.exercises.map { it.exercise.id }.toSet()
            val newExerciseIds = selectedExercises.map { it.exercise.id }.toSet()
            val removedExerciseIds = originalExerciseIds - newExerciseIds
            val addedExerciseIds = newExerciseIds - originalExerciseIds

            // Add removed exercises back to individual exercises
            val removedExercises = originalGroup.exercises.filter { it.exercise.id in removedExerciseIds }
            val exercisesRemovedFromIndividual = currentState.individualExercises.filter { it.exercise.id in addedExerciseIds }

            val updatedIndividualExercises = (currentState.individualExercises - exercisesRemovedFromIndividual.toSet()) + removedExercises

            // Update the group in the list
            val updatedGroups = currentState.exerciseGroups.map { group ->
                if (group.group.id == originalGroup.group.id) updatedGroup else group
            }

            currentState.copy(
                exerciseGroups = updatedGroups,
                individualExercises = updatedIndividualExercises,
                isGroupEditDialogVisible = false,
                groupBeingEdited = null
            )
        }
    }

    fun deleteExerciseGroup(group: RoutineExerciseGroup) {
        _uiState.update { currentState ->
            // Move group exercises back to individual exercises
            val exercisesToRestore = group.exercises.map { exercise ->
                exercise.copy(order = getNextExerciseOrder() + group.exercises.indexOf(exercise))
            }

            currentState.copy(
                exerciseGroups = currentState.exerciseGroups.filter { it.group.id != group.group.id },
                individualExercises = currentState.individualExercises + exercisesToRestore
            )
        }
    }

    fun moveGroupUp(index: Int) {
        if (index > 0) {
            val groups = _uiState.value.exerciseGroups.toMutableList()
            val group = groups.removeAt(index)
            groups.add(index - 1, group)
            _uiState.update { it.copy(exerciseGroups = groups) }
        }
    }

    fun moveGroupDown(index: Int) {
        val currentGroups = _uiState.value.exerciseGroups
        if (index < currentGroups.size - 1) {
            val groups = currentGroups.toMutableList()
            val group = groups.removeAt(index)
            groups.add(index + 1, group)
            _uiState.update { it.copy(exerciseGroups = groups) }
        }
    }

    // Timer Management (unchanged from original)
    fun onTimerIconClick(exerciseId: String) {
        val exercise = _uiState.value.individualExercises.find { it.exercise.id == exerciseId }
        _uiState.update {
            it.copy(
                isTimerSheetVisible = true,
                exerciseToSetTimer = exercise
            )
        }
    }

    fun onGroupTimerClick(group: RoutineExerciseGroup) {
        val placeholderExercise = RoutineExercise(
            exercise = group.exercises.first().exercise,
            restTimeSeconds = group.group.restTimeSeconds
        )
        _uiState.update {
            it.copy(
                isTimerSheetVisible = true,
                exerciseToSetTimer = placeholderExercise
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

        val isIndividualExercise = _uiState.value.individualExercises.any { it.exercise.id == exerciseId }

        if (isIndividualExercise) {
            _uiState.update { currentState ->
                val updatedExercises = currentState.individualExercises.map {
                    if (it.exercise.id == exerciseId) {
                        it.copy(restTimeSeconds = seconds)
                    } else {
                        it
                    }
                }
                currentState.copy(
                    individualExercises = updatedExercises,
                    isTimerSheetVisible = false,
                    exerciseToSetTimer = null
                )
            }
        } else {
            // Update group rest time
            _uiState.update { currentState ->
                val updatedGroups = currentState.exerciseGroups.map { group ->
                    if (group.exercises.any { it.exercise.id == exerciseId }) {
                        group.copy(
                            group = group.group.copy(restTimeSeconds = seconds)
                        )
                    } else {
                        group
                    }
                }
                currentState.copy(
                    exerciseGroups = updatedGroups,
                    isTimerSheetVisible = false,
                    exerciseToSetTimer = null
                )
            }
        }
    }

    // Helper Methods
    private fun getNextExerciseOrder(): Int {
        val maxIndividualOrder = _uiState.value.individualExercises.maxOfOrNull { it.order } ?: -1
        val maxGroupOrder = _uiState.value.exerciseGroups.maxOfOrNull { it.group.order } ?: -1
        return maxOf(maxIndividualOrder, maxGroupOrder) + 1
    }

    private fun getNextGroupOrder(): Int {
        return _uiState.value.exerciseGroups.maxOfOrNull { it.group.order } ?: 0
    }

    // Save Routine (Updated to handle groups)
    fun saveRoutine(onRoutineSaved: () -> Unit) {
        viewModelScope.launch {
            try {
                // Step 1: Save the routine and get its new ID
                val newRoutineId = routinesRepository.upsertRoutine(
                    Routine(name = _uiState.value.routineName)
                ).toInt()

                var currentOrder = 0

                // Step 2: Save exercise groups
                _uiState.value.exerciseGroups.forEach { routineExerciseGroup ->
                    // Create and save the group
                    val groupToSave = routineExerciseGroup.group.copy(
                        routineId = newRoutineId,
                        order = currentOrder++
                    )
                    val savedGroupId = routinesRepository.createExerciseGroup(groupToSave).toInt()

                    // Create cross-references for exercises in this group
                    val groupCrossRefs = routineExerciseGroup.exercises.mapIndexed { index, routineExercise ->
                        RoutineExerciseCrossRef(
                            routineId = newRoutineId,
                            exerciseId = routineExercise.exercise.id,
                            sets = routineExercise.sets,
                            reps = routineExercise.reps,
                            order = currentOrder, // Group's position in routine
                            restTimeSeconds = routineExercise.restTimeSeconds,
                            groupId = savedGroupId,
                            orderInGroup = index
                        )
                    }
                    routinesRepository.upsertRoutineExerciseCrossRefs(groupCrossRefs)
                }

                // Step 3: Save individual exercises
                if (_uiState.value.individualExercises.isNotEmpty()) {
                    val individualCrossRefs = _uiState.value.individualExercises.map { routineExercise ->
                        RoutineExerciseCrossRef(
                            routineId = newRoutineId,
                            exerciseId = routineExercise.exercise.id,
                            sets = routineExercise.sets,
                            reps = routineExercise.reps,
                            order = currentOrder++,
                            restTimeSeconds = routineExercise.restTimeSeconds,
                            groupId = null, // Individual exercise
                            orderInGroup = 0
                        )
                    }
                    routinesRepository.upsertRoutineExerciseCrossRefs(individualCrossRefs)
                }

                // Step 4: Navigate back
                onRoutineSaved()
            } catch (e: Exception) {
                // Handle error - could add error state to UI
                // For now, just log or show a simple error
                e.printStackTrace()
            }
        }
    }
}