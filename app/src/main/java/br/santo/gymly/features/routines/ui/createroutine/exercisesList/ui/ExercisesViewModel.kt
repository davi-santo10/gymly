package br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.Exercise
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseRepository
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.MuscleGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ExercisesUiState(
    val groupedExercises: Map<MuscleGroup, List<Exercise>> = emptyMap(),
    val selectedExerciseIds: Set<String> = emptySet(),
    val searchQuery: String = ""
)

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedIds = MutableStateFlow(
        savedStateHandle.get<String>("initialIds")
            ?.split(',')
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: emptySet()
    )

    private val muscleGroupOrder: List<MuscleGroup> = listOf(
        MuscleGroup.CHEST, MuscleGroup.LATS, MuscleGroup.FRONT_DELTS,
        MuscleGroup.REAR_DELTS, MuscleGroup.LOWER_BACK, MuscleGroup.QUADRICEPS,
        MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.CALVES,
        MuscleGroup.BICEPS, MuscleGroup.TRICEPS, MuscleGroup.FOREARMS,
        MuscleGroup.CORE, MuscleGroup.CARDIO
    )
    private val muscleGroupComparator = compareBy<MuscleGroup> { muscleGroupOrder.indexOf(it) }

    val uiState: StateFlow<ExercisesUiState> =
        combine(
            exerciseRepository.allExercises,
            _searchQuery,
            _selectedIds
        ) { allExercises, query, selectedIds ->
            val filteredExercises = if (query.isBlank()) {
                allExercises
            } else {
                allExercises.filter { exercise ->
                    exercise.name.contains(query, ignoreCase = true) ||
                            exercise.muscleGroup.name.replace('_', ' ').contains(query, ignoreCase = true)
                }
            }
            ExercisesUiState(
                groupedExercises = filteredExercises
                    .groupBy { it.muscleGroup }
                    .toSortedMap(muscleGroupComparator),
                selectedExerciseIds = selectedIds,
                searchQuery = query
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ExercisesUiState()
            )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleExerciseSelection(exerciseId: String) {
        _selectedIds.update { currentIds ->
            val newIds = currentIds.toMutableSet()
            if (exerciseId in newIds) {
                newIds.remove(exerciseId)
            } else {
                newIds.add(exerciseId)
            }
            newIds
        }
    }
}