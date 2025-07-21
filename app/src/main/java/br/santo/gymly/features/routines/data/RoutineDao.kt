package br.santo.gymly.features.routines.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Upsert
    suspend fun upsertRoutine(routine : Routine)

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Query("SELECT * FROM routines ORDER BY name ASC")
    fun getAllRoutines(): Flow<List<Routine>>

}