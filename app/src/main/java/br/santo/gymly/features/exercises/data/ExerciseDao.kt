package br.santo.gymly.features.exercises.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(exercise: Exercise)

    @Query("SELECT * FROM exercises ORDER BY name ASC") fun getAllExercises(): Flow<List<Exercise>>
}

