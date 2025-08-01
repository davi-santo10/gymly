package br.santo.gymly.features.routines.data

import androidx.room.Entity

@Entity(primaryKeys = ["routineId", "exerciseId"])
data class RoutineExerciseCrossRef(
    val routineId: Int,
    val exerciseId: String,
    val sets: String,
    val reps: String,
    val order: Int,
    val restTimeSeconds: Int = 60,

    val groupId: Int? = null,
    val orderInGroup: Int = 0
)