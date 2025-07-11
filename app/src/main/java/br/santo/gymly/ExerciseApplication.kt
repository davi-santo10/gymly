package br.santo.gymly

import android.app.Application
import br.santo.gymly.data.AppDatabase
import br.santo.gymly.data.ExerciseRepository

class ExerciseApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this)}
    val exerciseRepository by lazy { ExerciseRepository(database.exerciseDao()) }
}