package br.santo.gymly.features.routines.data

import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.Exercise

data class RoutineExercise (
    val exercise: Exercise,
    val sets: String = "3",
    val reps: String = "10",
    val restTime: String = "",
    val order: Int,
    val restTimeSeconds: Int = 60
    )