package br.santo.gymly.features.routines.ui.details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.data.RoutineWithExercises
import br.santo.gymly.features.routines.data.RoutinesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoutinesDetailsUiState(
    val routineWithExercises: RoutineWithExercises? = null,
    val isEditing: Boolean = false
)

class RoutineDetailsViewModel(
    private val routinesRepository: RoutinesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutinesDetailsUiState())
    val uiState: StateFlow<RoutinesDetailsUiState> = _uiState.asStateFlow()

    private val routineId: Int = checkNotNull(savedStateHandle["routineId"])

    init {
        viewModelScope.launch {
            routinesRepository.getRoutineWithExercises(routineId).collect { routineData ->
                _uiState.update { it.copy(routineWithExercises = routineData) }
            }
        }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing)}
    }
}