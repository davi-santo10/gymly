package br.santo.gymly.features.routines.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Upsert
    suspend fun upsertRoutine(routine : Routine): Long

    @Upsert
    suspend fun upsertRoutineExerciseCrossRefs(crossRefs: List<RoutineExerciseCrossRef>)

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Query("DELETE FROM RoutineExerciseCrossRef WHERE routineId = :routineId")
    suspend fun deleteCrossRefsForRoutine(routineId: Int)

    @Query("SELECT * FROM routines ORDER BY name ASC")
    fun getAllRoutines(): Flow<List<Routine>>

    @Transaction
    @Query("SELECT * FROM routines WHERE id = :routineId")
    fun getRoutineWithExercises(routineId: Int): Flow<RoutineWithExercises?>

    // ADD THIS NEW FUNCTION
    @Query("SELECT * FROM RoutineExerciseCrossRef WHERE routineId = :routineId")
    fun getCrossRefsForRoutine(routineId: Int): Flow<List<RoutineExerciseCrossRef>>

    @Query("""
        SELECT * FROM RoutineExerciseCrossRef 
        WHERE routineId = :routineId 
        ORDER BY 
            CASE WHEN groupId IS NULL THEN `order` ELSE groupId END,
            orderInGroup ASC
    """)
    fun getCrossRefsWithGroupsForRoutine(routineId: Int): Flow<List<RoutineExerciseCrossRef>>

    @Query("""
        SELECT * FROM RoutineExerciseCrossRef 
        WHERE routineId = :routineId AND groupId IS NULL
        ORDER BY `order` ASC
    """)
    fun getUngroupedCrossRefsForRoutine(routineId: Int): Flow<List<RoutineExerciseCrossRef>>


    @Query("""
        SELECT * FROM RoutineExerciseCrossRef 
        WHERE routineId = :routineId AND groupId = :groupId
        ORDER BY orderInGroup ASC
    """)
    fun getCrossRefsForGroup(routineId: Int, groupId: Int): Flow<List<RoutineExerciseCrossRef>>


    @Query("""
        UPDATE RoutineExerciseCrossRef 
        SET groupId = :groupId, orderInGroup = :orderInGroup
        WHERE routineId = :routineId AND exerciseId = :exerciseId
    """)
    suspend fun updateExerciseGroup(routineId: Int, exerciseId: String, groupId: Int?, orderInGroup: Int)


    @Query("""
        UPDATE RoutineExerciseCrossRef 
        SET groupId = NULL, orderInGroup = 0
        WHERE routineId = :routineId AND groupId = :groupId
    """)
    suspend fun removeExercisesFromGroup(routineId: Int, groupId: Int)

    @Query("""
        SELECT MAX(`order`) FROM RoutineExerciseCrossRef 
        WHERE routineId = :routineId AND groupId IS NULL
    """)
    suspend fun getMaxOrderForRoutine(routineId: Int): Int?
}