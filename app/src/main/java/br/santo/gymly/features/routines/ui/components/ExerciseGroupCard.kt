package br.santo.gymly.features.routines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.santo.gymly.features.routines.data.GroupType
import br.santo.gymly.features.routines.data.RoutineExerciseGroup
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.ui.components.MuscleGroupIconMapper
import java.util.Locale

@Composable
fun ExerciseGroupCard(
    exerciseGroup: RoutineExerciseGroup,
    isEditable: Boolean,
    onEditGroup: () -> Unit,
    onDeleteGroup: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onTimerClick: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (exerciseGroup.group.type) {
                GroupType.SUPERSET -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                GroupType.BISET -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                GroupType.TRISET -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                GroupType.CIRCUIT -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GroupTypeChip(groupType = exerciseGroup.group.type)
                        Text(
                            text = exerciseGroup.group.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "${exerciseGroup.exercises.size} exercises • ${formatRestTime(exerciseGroup.group.restTimeSeconds)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isEditable) {
                    Row {
                        IconButton(
                            onClick = onTimerClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Timer,
                                contentDescription = "Set Rest Timer",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onMoveUp,
                            enabled = !isFirst,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = "Move Up",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onMoveDown,
                            enabled = !isLast,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Move Down",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onEditGroup,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Group",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDeleteGroup,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Group",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                exerciseGroup.exercises.forEach { routineExercise ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = MuscleGroupIconMapper.map(routineExercise.exercise.muscleGroup)
                            ),
                            contentDescription = "${routineExercise.exercise.muscleGroup.name} icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = routineExercise.exercise.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${routineExercise.sets} sets × ${routineExercise.reps} reps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupTypeChip(
    groupType: GroupType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, text) = when (groupType) {
        GroupType.SUPERSET -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary,
            "SUPERSET"
        )
        GroupType.BISET -> Triple(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary,
            "BISET"
        )
        GroupType.TRISET -> Triple(
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary,
            "TRISET"
        )
        GroupType.CIRCUIT -> Triple(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError,
            "CIRCUIT"
        )
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(
                backgroundColor,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

private fun formatRestTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return when {
        minutes > 0 && remainingSeconds > 0 -> "${minutes}m ${remainingSeconds}s"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}