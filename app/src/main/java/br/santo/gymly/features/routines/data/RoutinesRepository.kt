package br.santo.gymly.features.routines.data

import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.Exercise
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RoutinesRepository(
    private val routineDao: RoutineDao,
    private val exerciseGroupDao: ExerciseGroupDao,
    private val exerciseDao: ExerciseDao
) {

    val allRoutines: Flow<List<Routine>> = routineDao.getAllRoutines()

    suspend fun upsertRoutine(routine: Routine): Long {
        return routineDao.upsertRoutine(routine)
    }

    suspend fun deleteRoutine(routine: Routine) {
        exerciseGroupDao.deleteGroupsForRoutine(routine.id)
        routineDao.deleteCrossRefsForRoutine(routine.id)
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


    suspend fun createExerciseGroup(group: ExerciseGroup): Long {
        return exerciseGroupDao.insertGroup(group)
    }

    suspend fun updateExerciseGroup(group: ExerciseGroup) {
        exerciseGroupDao.updateGroup(group)
    }

    suspend fun deleteExerciseGroup(groupId: Int, routineId: Int) {
        routineDao.removeExercisesFromGroup(routineId, groupId)

        val group = exerciseGroupDao.getGroupById(groupId)
        if (group != null) {
            exerciseGroupDao.deleteGroup(group)
        }
    }

    suspend fun addExercisesToGroup(
        routineId: Int,
        groupId: Int,
        exerciseIds: List<String>
    ) {
        exerciseIds.forEachIndexed { index, exerciseId ->
            routineDao.updateExerciseGroup(
                routineId = routineId,
                exerciseId = exerciseId,
                groupId = groupId,
                orderInGroup = index
            )
        }
    }

    suspend fun removeExercisesFromGroup(routineId: Int, groupId: Int) {
        routineDao.removeExercisesFromGroup(routineId, groupId)
    }


    fun getRoutineWithGroups(routineId: Int): Flow<RoutineWithGroups?> {
        val routineFlow = routineDao.getRoutineWithExercises(routineId)
        val groupsFlow = exerciseGroupDao.getGroupsForRoutine(routineId)
        val crossRefsFlow = routineDao.getCrossRefsWithGroupsForRoutine(routineId)

        return combine(routineFlow, groupsFlow, crossRefsFlow) { routineWithExercises, groups, crossRefs ->
            if (routineWithExercises == null) {
                null
            } else {
                buildRoutineWithGroups(routineWithExercises, groups, crossRefs)
            }
        }
    }

    private suspend fun buildRoutineWithGroups(
        routineWithExercises: RoutineWithExercises,
        groups: List<ExerciseGroup>,
        crossRefs: List<RoutineExerciseCrossRef>
    ): RoutineWithGroups {
        val exerciseMap = routineWithExercises.exercises.associateBy { it.id }

        val exerciseGroups = groups.map { group ->
            val groupCrossRefs = crossRefs.filter { it.groupId == group.id }
                .sortedBy { it.orderInGroup }

            val groupExercises = groupCrossRefs.mapNotNull { crossRef ->
                exerciseMap[crossRef.exerciseId]?.let { exercise ->
                    RoutineExercise(
                        exercise = exercise,
                        sets = crossRef.sets,
                        reps = crossRef.reps,
                        order = crossRef.order,
                        restTimeSeconds = crossRef.restTimeSeconds
                    )
                }
            }

            RoutineExerciseGroup(
                group = group,
                exercises = groupExercises
            )
        }

        val ungroupedCrossRefs = crossRefs.filter { it.groupId == null }
        val individualExercises = ungroupedCrossRefs.mapNotNull { crossRef ->
            exerciseMap[crossRef.exerciseId]?.let { exercise ->
                RoutineExercise(
                    exercise = exercise,
                    sets = crossRef.sets,
                    reps = crossRef.reps,
                    order = crossRef.order,
                    restTimeSeconds = crossRef.restTimeSeconds
                )
            }
        }

        return RoutineWithGroups(
            routine = routineWithExercises.routine,
            exerciseGroups = exerciseGroups,
            individualExercises = individualExercises
        )
    }

    suspend fun getNextGroupOrder(routineId: Int): Int {
        val maxOrder = exerciseGroupDao.getMaxOrderForRoutine(routineId) ?: -1
        return maxOrder + 1
    }

    suspend fun getNextExerciseOrder(routineId: Int): Int {
        val maxOrder = routineDao.getMaxOrderForRoutine(routineId) ?: -1
        return maxOrder + 1
    }
}