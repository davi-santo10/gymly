package br.santo.gymly.features.routines.ui.createroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseRepository
import br.santo.gymly.features.routines.data.RoutinesRepository

class CreateRoutineViewModelFactory(
    private val routinesRepository: RoutinesRepository,
    private val exerciseRepository: ExerciseRepository
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateRoutineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateRoutineViewModel(routinesRepository, exerciseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}