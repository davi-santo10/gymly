package br.santo.gymly.features.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.santo.gymly.features.exercises.data.ExerciseRepository


class ExercisesViewModelFactory(
    private val repository: ExerciseRepository,
    private val initialIds: Set<String>
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExercisesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExercisesViewModel(repository,initialIds) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}