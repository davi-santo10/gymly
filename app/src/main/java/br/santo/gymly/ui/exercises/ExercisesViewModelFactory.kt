package br.santo.gymly.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.santo.gymly.data.ExerciseRepository

/**
 * A Factory class for creating an instance of our ExercisesViewModel.
 *
 * Why we need this:
 * By default, the system can only create ViewModels that have no constructor arguments.
 * Our ExercisesViewModel NEEDS an ExerciseRepository to do its job.
 * This factory tells the system, "Hey, when you need to create an ExercisesViewModel,
 * use this special process: take this repository I'm giving you and pass it into the
 * ViewModel's constructor."
 */
class ExercisesViewModelFactory(private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is of the correct type (ExercisesViewModel)
        if (modelClass.isAssignableFrom(ExercisesViewModel::class.java)) {
            // If it is, create and return an instance of it, passing the repository.
            // The @Suppress("UNCHECKED_CAST") is safe here because we've already checked the class type.
            @Suppress("UNCHECKED_CAST")
            return ExercisesViewModel(repository) as T
        }
        // If someone asks for a different kind of ViewModel, throw an error.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}