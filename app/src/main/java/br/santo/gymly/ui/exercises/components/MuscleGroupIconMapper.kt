package br.santo.gymly.ui.exercises.components

import androidx.annotation.DrawableRes
import br.santo.gymly.R
import br.santo.gymly.data.MuscleGroup

object MuscleGroupIconMapper {
    @DrawableRes
    fun map(muscleGroup: MuscleGroup): Int {
        return when (muscleGroup) {
            MuscleGroup.BICEPS -> R.drawable.biceps
            MuscleGroup.CALVES -> R.drawable.calves
            MuscleGroup.CARDIO -> R.drawable.cardio
            MuscleGroup.CHEST -> R.drawable.chest
            MuscleGroup.CORE -> R.drawable.core
            MuscleGroup.FOREARMS -> R.drawable.forearms
            MuscleGroup.FRONT_DELTS -> R.drawable.front_delts
            MuscleGroup.GLUTES -> R.drawable.glutes
            MuscleGroup.HAMSTRINGS -> R.drawable.hamstrings
            MuscleGroup.LATS -> R.drawable.lats
            MuscleGroup.LOWER_BACK -> R.drawable.lowerback
            MuscleGroup.QUADRICEPS -> R.drawable.quads
            MuscleGroup.REAR_DELTS -> R.drawable.rear_delts
            MuscleGroup.TRICEPS -> R.drawable.triceps
        }
        }
    }
