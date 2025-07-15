package br.santo.gymly.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.data.Exercise
import br.santo.gymly.data.ExerciseRepository
import br.santo.gymly.data.MuscleGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
data class ExercisesUiState(
    val groupedExercises: Map<MuscleGroup, List<Exercise>> = emptyMap()
)

class ExercisesViewModel(exerciseRepository: ExerciseRepository) : ViewModel() {
    private val muscleGroupOrder: List<MuscleGroup> = listOf(
        MuscleGroup.CHEST,
        MuscleGroup.LATS,
        MuscleGroup.FRONT_DELTS,
        MuscleGroup.REAR_DELTS,
        MuscleGroup.LOWER_BACK,
        MuscleGroup.QUADS,
        MuscleGroup.HAMSTRINGS,
        MuscleGroup.GLUTES,
        MuscleGroup.CALVES,
        MuscleGroup.BICEPS,
        MuscleGroup.TRICEPS,
        MuscleGroup.FOREARMS,
        MuscleGroup.CORE,
        MuscleGroup.CARDIO
    )
    private val muscleGroupComparator = Comparator<MuscleGroup> { group1, group2 ->
        muscleGroupOrder.indexOf(group1).compareTo(muscleGroupOrder.indexOf(group2))
    }
    private val _searchQuery = MutableStateFlow("")
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    val uiState: StateFlow<ExercisesUiState> =
        combine(exerciseRepository.allExercises, _searchQuery) { allExercises, query ->

            // 4. Apply the filtering logic.
            val filteredExercises = if (query.isBlank()) {
                allExercises
            } else {
                allExercises.filter { exercise ->
                    // --- THIS IS THE ONLY CHANGE ---
                    // Check if the exercise name OR the muscle group name contains the query.
                    exercise.name.contains(query, ignoreCase = true) ||
                            exercise.muscleGroup.name.contains(query, ignoreCase = true)


                }
            }

            // 5. Group and sort the *filtered* list.
            ExercisesUiState(
                groupedExercises = filteredExercises
                    .groupBy { it.muscleGroup }
                    .toSortedMap(muscleGroupComparator)
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ExercisesUiState()
            )
}