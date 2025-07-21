package br.santo.gymly.features.routines.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.Exercise

data class RoutineWithExercises(
    @Embedded val routine: Routine,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RoutineExerciseCrossRef::class,
            parentColumn = "routineId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<Exercise>
)