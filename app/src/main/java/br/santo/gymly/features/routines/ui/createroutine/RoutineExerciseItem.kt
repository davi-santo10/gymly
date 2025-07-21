package br.santo.gymly.features.routines.ui.createroutine

import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.santo.gymly.features.routines.data.RoutineExercise

@Composable
fun RoutineExerciseItem(
    routineExercise: RoutineExercise,
    onSetsChanged: (String) -> Unit,
    onRepsChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier.fillMaxWidth()
    ) {
        Column (
           modifier = Modifier.padding(16.dp)
        ) {
           Text(
              text = routineExercise.exercise.name,
               style = MaterialTheme.typography.titleMedium
           )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = routineExercise.sets,
                    onValueChange = onSetsChanged,
                    label = { Text("Sets")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField (
                    value = routineExercise.reps,
                    onValueChange = onRepsChanged,
                    label = { Text ("Reps")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

            }
        }
    }
}