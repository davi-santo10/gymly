package br.santo.gymly.features.exercises.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "exercises")
@TypeConverters(ExerciseTypeConverter::class)
data class Exercise(
    @PrimaryKey val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val restTime: Int?,
    val iconResId: Int,
)

object ExerciseTypeConverter {
    @TypeConverter @JvmStatic fun fromMuscleGroup(value: MuscleGroup): String = value.name

    @TypeConverter
    @JvmStatic
    fun toMuscleGroup(value: String): MuscleGroup = MuscleGroup.valueOf(value)

    @TypeConverter @JvmStatic fun fromExerciseType(value: ExerciseType): String = value.name

    @TypeConverter
    @JvmStatic
    fun toExerciseType(value: String): ExerciseType = ExerciseType.valueOf(value)
}
