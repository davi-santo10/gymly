package br.santo.gymly.features.routines.ui.routinelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.santo.gymly.features.routines.data.RoutinesRepository

class RoutinesViewModelFactory(private val repository: RoutinesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutinesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutinesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}