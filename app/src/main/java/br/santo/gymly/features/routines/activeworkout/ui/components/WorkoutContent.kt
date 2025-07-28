package br.santo.gymly.features.routines.activeworkout.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.santo.gymly.features.routines.activeworkout.data.ActiveWorkout

@Composable
fun WorkoutContent(
    activeWorkout: ActiveWorkout,
    onExerciseClick: (Int) -> Unit,
    onSetRepsChange: (Int, Int, Int) -> Unit,
    onSetWeightChange: (Int, Int, Float) -> Unit,
    onSetCompletionToggle: (Int, Int) -> Unit,
    onFinishWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Scrollable list of exercises
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = activeWorkout.exercises,
                key = { _, exercise -> exercise.exercise.id }
            ) { index, activeExercise ->
                ActiveExerciseCard(
                    activeExercise = activeExercise,
                    onExerciseClick = { onExerciseClick(index) },
                    onSetRepsChange = { setIndex, reps ->
                        onSetRepsChange(index, setIndex, reps)
                    },
                    onSetWeightChange = { setIndex, weight ->
                        onSetWeightChange(index, setIndex, weight.toFloat())
                    },
                    onSetCompletionToggle = { setIndex ->
                        onSetCompletionToggle(index, setIndex)
                    }
                )
            }
        }

        Button(
            onClick = onFinishWorkout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = activeWorkout.exercises.isNotEmpty()
        ) {
            Text(
                text = "Finish Workout",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}