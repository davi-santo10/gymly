package br.santo.gymly.data

import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao : ExerciseDao) {
    val allExercises : Flow<List<Exercise>> = exerciseDao.getAllExercises()
}