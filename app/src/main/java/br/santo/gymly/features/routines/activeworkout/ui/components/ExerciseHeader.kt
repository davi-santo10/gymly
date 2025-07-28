package br.santo.gymly.features.routines.activeworkout.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.santo.gymly.features.routines.activeworkout.data.ActiveWorkoutExercise
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui.components.MuscleGroupIconMapper
import java.util.Locale

@Composable
fun ExerciseHeader (
    exercise: ActiveWorkoutExercise,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painter = painterResource(id = MuscleGroupIconMapper.map(exercise.exercise.muscleGroup)),
            contentDescription = "${exercise.exercise.muscleGroup.name} icon",
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified
        )

        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text (
                text = exercise.exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = exercise.exercise.muscleGroup.name
                    .replace('_', ' ')
                    .lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()},
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Checkbox(
            checked = exercise.isCompleted,
            onCheckedChange = null,
            enabled = false
        )
    }
}