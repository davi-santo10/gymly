package br.santo.gymly.features.routines.data

import kotlinx.coroutines.flow.Flow

class RoutinesRepository(private val routineDao: RoutineDao) {

    val allRoutines: Flow<List<Routine>> = routineDao.getAllRoutines()

    suspend fun upsertRoutine(routine: Routine) {
        routineDao.upsertRoutine(routine)
    }

    suspend fun deleteRoutine(routine: Routine) {
        routineDao.deleteRoutine(routine)
    }
}