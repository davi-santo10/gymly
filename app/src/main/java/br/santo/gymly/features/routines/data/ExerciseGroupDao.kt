package br.santo.gymly.features.routines.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseGroupDao {

    @Insert
    suspend fun insertGroup(group: ExerciseGroup): Long

    @Update
    suspend fun updateGroup(group: ExerciseGroup)

    @Delete
    suspend fun deleteGroup(group: ExerciseGroup)

    @Query("SELECT * FROM exercise_groups WHERE routineId = :routineId ORDER BY 'order' ASC")
    fun getGroupsForRoutine(routineId: Int): Flow<List<ExerciseGroup>>

    @Query("SELECT * FROM exercise_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Int): ExerciseGroup?

    @Query("DELETE FROM exercise_groups WHERE routineid = :routineId")
    suspend fun deleteGroupsForRoutine(routineId: Int)

    @Query("SELECT MAX('order') FROM exercise_groups WHERE routineId = :routineId")
    suspend fun getMaxOrderForRoutine(routineId: Int): Int?
}