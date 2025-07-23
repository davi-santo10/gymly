package br.santo.gymly.features.routines.ui.createroutine.exercisesList.data

import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun getExercisesByIds(ids: List<String>): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByIds(ids)
    }
}

