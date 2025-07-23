package br.santo.gymly.features.routines.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.santo.gymly.ExerciseApplication

class RoutineDetailsViewModelFactory {
    object Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application =
                checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as ExerciseApplication
            val savedStateHandle = extras.createSavedStateHandle()

            return RoutineDetailsViewModel(
                application.routinesRepository,
                application.exerciseRepository,
                savedStateHandle
            ) as T
        }
    }
}