package br.santo.gymly.features.routines.activeworkout.data

import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.Exercise

data class WorkoutSet (
    val setNumber: Int,
    val targetReps: Int,
    val actualReps: Int = 0,
    val weight: Float = 0f,
    val isCompleted: Boolean = false
)

data class ActiveWorkoutExercise(
    val exercise: Exercise,
    val sets: List<WorkoutSet>,
    val isExpanded: Boolean = false,
    val targetSets: Int,
    val restTimeSeconds: Int = 60
) {
    val isCompleted: Boolean
        get() = sets.all { it.isCompleted}

    val completionProgress: Float
        get() = if (sets.isEmpty()) 0f else sets.count { it.isCompleted }.toFloat() / sets.size
}

data class ActiveWorkout(
    val routineId: Int,
    val routineName: String,
    val exercises: List<ActiveWorkoutExercise>,
    val startTime: Long = System.currentTimeMillis(),
    val isFinished: Boolean = false
) {
    val overallProgress : Float
        get() = if (exercises.isEmpty()) 0f
            else exercises.sumOf { it.completionProgress.toDouble()}.toFloat() / exercises.size

    val durationMinutes : Long
        get() = (System.currentTimeMillis() - startTime) / (1000 * 60)
}