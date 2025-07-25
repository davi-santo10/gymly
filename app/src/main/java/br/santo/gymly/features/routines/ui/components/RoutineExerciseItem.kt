package br.santo.gymly.features.routines.ui.components

import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.santo.gymly.features.routines.data.RoutineExercise
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui.components.MuscleGroupIconMapper

@Composable
fun RoutineExerciseItem(
    modifier: Modifier = Modifier,
    routineExercise: RoutineExercise,
    isEditable: Boolean,
    onSetsChanged: (String) -> Unit,
    onRepsChanged: (String) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onTimerClick: (String) -> Unit,
    isFirst: Boolean,
    isLast: Boolean
) {
    Card(modifier = modifier.fillMaxWidth()) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val (titleRef, upArrowRef, downArrowRef, timerRef, setsRef, repsRef, iconRef) = createRefs()

            Text(
                text = routineExercise.exercise.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.constrainAs(titleRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(timerRef.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }
            )

            IconButton(
                onClick = onMoveDown, enabled = isEditable && !isLast,
                modifier = Modifier.size(32.dp).constrainAs(downArrowRef) {
                    end.linkTo(parent.end)
                    centerVerticallyTo(titleRef)
                }
            ) {
                if (isEditable) {
                    Icon(Icons.Default.KeyboardArrowDown, "Move Down")
                }
            }

            IconButton(
                onClick = onMoveUp, enabled = isEditable && !isFirst,
                modifier = Modifier.size(32.dp).constrainAs(upArrowRef) {
                    end.linkTo(downArrowRef.start)
                    centerVerticallyTo(titleRef)
                }
            ) {
                if (isEditable) {
                    Icon(Icons.Default.KeyboardArrowUp, "Move Up")
                }
            }

            IconButton(
                onClick = { onTimerClick(routineExercise.exercise.id)},
                modifier = Modifier.size(32.dp).constrainAs(timerRef) {
                    end.linkTo(upArrowRef.start)
                    centerVerticallyTo(titleRef)
                }
            ) {
                if (isEditable) {
                    Icon(Icons.Outlined.Timer, contentDescription = "Set Rest Timer")
                }
            }


            OutlinedTextField(
                value = routineExercise.sets,
                onValueChange = onSetsChanged,
                label = { Text("Sets") },
                readOnly = !isEditable,
                modifier = Modifier.constrainAs(setsRef) {
                    top.linkTo(titleRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(repsRef.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }
            )
            OutlinedTextField(
                value = routineExercise.reps,
                onValueChange = onRepsChanged,
                label = { Text("Reps") },
                readOnly = !isEditable,
                modifier = Modifier.constrainAs(repsRef) {
                    top.linkTo(setsRef.top)
                    start.linkTo(setsRef.end)
                    end.linkTo(iconRef.start, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Icon(
                painter = painterResource(id = MuscleGroupIconMapper.map(routineExercise.exercise.muscleGroup)),
                contentDescription = "${routineExercise.exercise.muscleGroup.name} icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp).constrainAs(iconRef) {
                    centerVerticallyTo(setsRef)
                    start.linkTo(upArrowRef.start)
                    end.linkTo(downArrowRef.end)
                }
            )
        }
    }
}