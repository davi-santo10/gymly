package br.santo.gymly.features.routines.data

import br.santo.gymly.features.routines.ui.routinelist.RoutineItem
import kotlin.collections.map

data class RoutineWithGroups(
    val routine: Routine,
    val exerciseGroups: List<RoutineExerciseGroup>,
    val individualExercises: List<RoutineExercise>
) {
    val allItems: List<RoutineItem>
        get() {
            val items = mutableListOf<RoutineItem>()
            items.addAll(exerciseGroups.map { RoutineItem.Group(it)})
            items.addAll(individualExercises.map { RoutineItem.Individual(it)})
            return items.sortedBy {
                when (it) {
                    is RoutineItem.Group -> it.group.group.order
                    is RoutineItem.Individual -> it.exercise.order
                }
            }
        }
}

data class RoutineExerciseGroup (
    val group: ExerciseGroup,
    val exercises: List<RoutineExercise>
)
sealed class RoutineItem {
    data class Group(val group: RoutineExerciseGroup) : RoutineItem()
    data class Individual(val exercise: RoutineExercise) : RoutineItem()
}