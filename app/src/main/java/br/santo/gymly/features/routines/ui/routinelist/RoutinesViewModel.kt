package br.santo.gymly.features.routines.ui.routinelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutinesUiState(
    val routines: List<Routine> = emptyList(),
    val isDeleting: Boolean = false,
    val deleteError: String? = null
)
@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val repository: RoutinesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutinesUiState())
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allRoutines.collect { routines ->
                _uiState.update { currentState ->
                    currentState.copy(routines = routines)
                }
            }
        }
    }

    fun deleteRoutine(routine: Routine, onDeleteComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isDeleting = true, deleteError = null)}

                repository.deleteCrossRefsForRoutine(routine.id)
                repository.deleteRoutine(routine)
                _uiState.update { it.copy(isDeleting = false)}

                onDeleteComplete?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteError = "Failed to delete routine : ${e.message}"
                    )
                }
            }
        }
    }

    fun clearDeleteError () {
        _uiState.update { it.copy(deleteError = null)}
    }
}

