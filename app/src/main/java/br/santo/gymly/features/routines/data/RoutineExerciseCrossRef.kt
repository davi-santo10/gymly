package br.santo.gymly.features.routines.data

import androidx.room.Entity

@Entity(primaryKeys = ["routineId", "exerciseId"])
data class RoutineExerciseCrossRef(
    val routineId: Int,
    val exerciseId: String,
    val sets: String,
    val reps: String
)