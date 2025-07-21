package br.santo.gymly.features.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.exercises.data.Exercise
import br.santo.gymly.features.exercises.data.ExerciseRepository
import br.santo.gymly.features.exercises.data.MuscleGroup
import kotlinx.coroutines.flow.*

data class ExercisesUiState(
    val groupedExercises: Map<MuscleGroup, List<Exercise>> = emptyMap(),
    val selectedExerciseIds: Set<String> = emptySet(),
    val searchQuery: String= ""
)

class ExercisesViewModel(
    exerciseRepository: ExerciseRepository,
    initialIds: Set<String>,
) : ViewModel() {

    // Fluxos de dados privados para a busca e a seleção
    private val _searchQuery = MutableStateFlow("")
    private val _selectedIds = MutableStateFlow(initialIds) // Inicializa com os IDs recebidos!

    // A sua lógica de ordenação
    private val muscleGroupOrder: List<MuscleGroup> = listOf(
        MuscleGroup.CHEST, MuscleGroup.LATS, MuscleGroup.FRONT_DELTS,
        MuscleGroup.REAR_DELTS, MuscleGroup.LOWER_BACK, MuscleGroup.QUADRICEPS,
        MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.CALVES,
        MuscleGroup.BICEPS, MuscleGroup.TRICEPS, MuscleGroup.FOREARMS,
        MuscleGroup.CORE, MuscleGroup.CARDIO
    )
    private val muscleGroupComparator = compareBy<MuscleGroup> { muscleGroupOrder.indexOf(it) }

    // O uiState é criado combinando os três fluxos de dados
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
            // Cria o estado final com os exercícios filtrados E os IDs selecionados
            ExercisesUiState(
                groupedExercises = filteredExercises
                    .groupBy { it.muscleGroup }
                    .toSortedMap(muscleGroupComparator),
                selectedExerciseIds = selectedIds
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

    // Função para adicionar ou remover um ID da seleção
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