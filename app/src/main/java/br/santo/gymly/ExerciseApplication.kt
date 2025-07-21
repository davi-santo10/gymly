package br.santo.gymly

import android.app.Application
import br.santo.gymly.core.data.AppDatabase
import br.santo.gymly.features.exercises.data.ExerciseRepository
import br.santo.gymly.features.routines.data.RoutinesRepository

class ExerciseApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val exerciseRepository by lazy { ExerciseRepository(database.exerciseDao()) }

    val routinesRepository by lazy { RoutinesRepository(database.routineDao()) }
}
