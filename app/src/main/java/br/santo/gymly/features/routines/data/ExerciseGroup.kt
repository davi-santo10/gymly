package br.santo.gymly.features.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_groups")
data class ExerciseGroup (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routineId: Int,
    val name: String,
    val restTimeSeconds: Int = 60,
    val order: Int,
    val type: GroupType = GroupType.SUPERSET
)

enum class GroupType {
    SUPERSET,
    BISET,
    TRISET,
    CIRCUIT,
}