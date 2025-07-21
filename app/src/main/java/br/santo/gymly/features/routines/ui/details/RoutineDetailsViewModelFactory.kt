package br.santo.gymly.features.routines.ui.details

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.santo.gymly.ExerciseApplication

object RoutineDetailsViewModelFactory {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val savedStateHandle = createSavedStateHandle()
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ExerciseApplication)
            RoutineDetailsViewModel(
                routinesRepository = application.routinesRepository,
                savedStateHandle = savedStateHandle
            )
        }
    }
}