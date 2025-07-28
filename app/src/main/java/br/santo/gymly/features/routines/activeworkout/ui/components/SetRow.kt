package br.santo.gymly.features.routines.activeworkout.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.santo.gymly.features.routines.activeworkout.data.WorkoutSet

@Composable
fun SetRow(
    setNumber: Int,
    workoutSet: WorkoutSet,
    targetReps: Int,
    onRepsChange: (Int) -> Unit,
    onWeightChange: (Float) -> Unit,
    onCompletionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var repsText by remember(workoutSet.actualReps) {
        mutableStateOf(if (workoutSet.actualReps == 0) "" else workoutSet.actualReps.toString())
    }
    var weightText by remember(workoutSet.weight) {
        mutableStateOf(if (workoutSet.weight == 0f) "" else workoutSet.weight.toString())
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = setNumber.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = repsText,
            onValueChange = { newValue ->
                repsText = newValue
                newValue.toIntOrNull()?.let { reps ->
                    onRepsChange(reps)
                }
            },
            placeholder = { Text(targetReps.toString()) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        OutlinedTextField(
            value = weightText,
            onValueChange = { newValue ->
                weightText = newValue
                newValue.toFloatOrNull()?.let { weight ->
                    onWeightChange(weight)
                }
            },
            placeholder = { Text("0.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Checkbox(
            checked = workoutSet.isCompleted,
            onCheckedChange = { onCompletionToggle() },
            modifier = Modifier.width(80.dp)
        )
    }
}