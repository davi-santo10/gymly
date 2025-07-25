package br.santo.gymly.features.routines.ui.routinelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RoutinesUiState(
    val routines: List<Routine> = emptyList()
)
@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val repository: RoutinesRepository
) : ViewModel() {
    val uiState: StateFlow<RoutinesUiState> = repository.allRoutines
        .map { routines ->
            RoutinesUiState(routines)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RoutinesUiState()
        )
}

