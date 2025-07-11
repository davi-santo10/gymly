package br.santo.gymly.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.data.Exercise
import br.santo.gymly.data.ExerciseRepository
import br.santo.gymly.data.MuscleGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// This data class will represent the full state of our screen
data class ExercisesUiState(
    val groupedExercises: Map<MuscleGroup, List<Exercise>> = emptyMap()
)

class ExercisesViewModel(exerciseRepository: ExerciseRepository) : ViewModel() {

    /**
     * This StateFlow holds the current state of our UI.
     * The UI will "collect" this flow and automatically update whenever the state changes.
     */
    val uiState: StateFlow<ExercisesUiState> =
        // Start with the flow of all exercises from the repository
        exerciseRepository.allExercises
            // Use 'map' to transform the flat list into the grouped data structure our UI needs
            .map { exercises ->
                ExercisesUiState(
                    groupedExercises = exercises.groupBy { it.muscleGroup }
                )
            }
            // Convert the regular Flow into a StateFlow that the UI can more easily collect.
            .stateIn(
                scope = viewModelScope, // The lifecycle scope of this ViewModel
                // Start collecting when the UI is visible, stop 5 seconds after it's gone.
                started = SharingStarted.WhileSubscribed(5000),
                // The initial state before any data has loaded from the database.
                initialValue = ExercisesUiState()
            )
}