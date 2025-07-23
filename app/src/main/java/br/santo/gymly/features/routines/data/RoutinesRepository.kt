package br.santo.gymly.features.routines.data

import kotlinx.coroutines.flow.Flow

class RoutinesRepository(private val routineDao: RoutineDao) {

    val allRoutines: Flow<List<Routine>> = routineDao.getAllRoutines()

    suspend fun upsertRoutine(routine: Routine): Long {
        return routineDao.upsertRoutine(routine)
    }

    suspend fun deleteRoutine(routine: Routine) {
        routineDao.deleteRoutine(routine)
    }

    suspend fun deleteCrossRefsForRoutine(routineId: Int) {
        routineDao.deleteCrossRefsForRoutine(routineId)
    }

    suspend fun upsertRoutineExerciseCrossRefs(crossRefs: List<RoutineExerciseCrossRef>) {
        routineDao.upsertRoutineExerciseCrossRefs(crossRefs)
    }

    fun getRoutineWithExercises(routineId: Int): Flow<RoutineWithExercises?> {
        return routineDao.getRoutineWithExercises(routineId)
    }

    fun getCrossRefsForRoutine(routineId: Int): Flow<List<RoutineExerciseCrossRef>> {
        return routineDao.getCrossRefsForRoutine(routineId)
    }
}