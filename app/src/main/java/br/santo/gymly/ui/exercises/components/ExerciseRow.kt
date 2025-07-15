package br.santo.gymly.ui.exercises.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.santo.gymly.data.Exercise

@Composable
fun ExerciseRow(
    exercise: Exercise,
    modifier: Modifier = Modifier
){
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon (
            painter = painterResource(id = MuscleGroupIconMapper.map(exercise.muscleGroup)),
            contentDescription = "${exercise.muscleGroup.name} icon",
            modifier = Modifier.size(40.dp),
            tint = Color.Unspecified

        )
        Text (
            text = exercise.name,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}