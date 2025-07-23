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
}