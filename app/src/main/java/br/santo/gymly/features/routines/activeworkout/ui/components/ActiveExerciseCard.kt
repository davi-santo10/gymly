package br.santo.gymly.features.routines.activeworkout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.santo.gymly.features.routines.activeworkout.data.ActiveWorkoutExercise

@Composable
fun ActiveExerciseCard(
    activeExercise: ActiveWorkoutExercise,
    onExerciseClick: () -> Unit,
    onSetRepsChange: (setIndex: Int, reps: Int) -> Unit,
    onSetWeightChange: (setIndex: Int, weight: Float) -> Unit, // Changed to Float
    onSetCompletionToggle: (setIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onExerciseClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activeExercise.isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ExerciseHeader(
                exercise = activeExercise,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = activeExercise.isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    SetsTableHeader()

                    // Fixed: forEachIndexed (lowercase 'f')
                    activeExercise.sets.forEachIndexed { setIndex, workoutSet ->
                        SetRow(
                            setNumber = workoutSet.setNumber,
                            workoutSet = workoutSet,
                            targetReps = workoutSet.targetReps,
                            // Fixed: onRepsChange (capital 'C')
                            onRepsChange = { reps ->
                                onSetRepsChange(setIndex, reps)
                            },
                            onWeightChange = { weight ->
                                onSetWeightChange(setIndex, weight)
                            },
                            onCompletionToggle = {
                                onSetCompletionToggle(setIndex)
                            }
                        )
                    }
                }
            }
        }
    }
}