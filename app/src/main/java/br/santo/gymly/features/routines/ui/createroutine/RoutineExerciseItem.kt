package br.santo.gymly.features.routines.ui.createroutine

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
    isEditable: Boolean,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column for Title, Sets, and Reps
            Column(modifier = Modifier.weight(1f)) {
                // Exercise Name
                Text(
                    text = routineExercise.exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Row to hold the editable fields or static text
                Row(
                    // MODIFIED: Spacing reduced from 16.dp to 8.dp
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isEditable) {
                        // Editable state with OutlinedTextFields
                        OutlinedTextField(
                            value = routineExercise.sets,
                            onValueChange = onSetsChanged,
                            label = { Text("Sets") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = routineExercise.reps,
                            onValueChange = onRepsChanged,
                            label = { Text("Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Non-editable state with styled Text
                        Column{
                            Text(
                                text = "Sets",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = routineExercise.sets,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Column (modifier = Modifier.padding(horizontal = 8.dp)){
                            Text(
                                text = "Reps",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = routineExercise.reps,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}