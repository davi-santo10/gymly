package br.santo.gymly.features.routines.activeworkout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.santo.gymly.features.routines.activeworkout.ui.RestTimerState
import java.util.Locale

/**
 * A compact timer bar that appears at the bottom of the screen, similar to Material 3 toolbar.
 * Shows rest time in a minimal, non-intrusive way with essential controls.
 *
 * @param timerState The current state of the rest timer (null if no timer is active)
 * @param onTogglePause Function to pause/resume the timer
 * @param onStopTimer Function to stop and dismiss the timer
 * @param modifier Modifier for customizing the appearance
 */
@Composable
fun RestTimerDisplay(
    timerState: RestTimerState?,
    onTogglePause: () -> Unit,
    onStopTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // AnimatedVisibility with slide up/down animation from bottom
    AnimatedVisibility(
        visible = timerState != null,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight }, // Start from below screen
            animationSpec = tween(durationMillis = 250)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight }, // Exit below screen
            animationSpec = tween(durationMillis = 250)
        ),
        modifier = modifier
    ) {
        timerState?.let { timer ->
            CompactTimerBar(
                timerState = timer,
                onTogglePause = onTogglePause,
                onStopTimer = onStopTimer
            )
        }
    }
}

@Composable
private fun CompactTimerBar(
    timerState: RestTimerState,
    onTogglePause: () -> Unit,
    onStopTimer: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp)),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 3.dp,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RestIndicatorDot(isRunning = timerState.isRunning)

                TimerText(remainingSeconds = timerState.remainingTimeSeconds)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompactIconButton(
                    onClick = onTogglePause,
                    icon = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (timerState.isRunning) "Pause timer" else "Resume timer"
                )

                CompactIconButton(
                    onClick = onStopTimer,
                    icon = Icons.Default.Stop,
                    contentDescription = "Stop timer"
                )
            }
        }
    }
}


@Composable
private fun RestIndicatorDot(isRunning: Boolean) {
    val dotColor = if (isRunning) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(8.dp)
    ) {
        drawCircle(color = dotColor)
    }
}


@Composable
private fun TimerText(remainingSeconds: Int) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)

    Text(
        text = timeText,
        style = MaterialTheme.typography.titleMedium, // Larger text
        fontWeight = FontWeight.SemiBold, // More prominent
        color = MaterialTheme.colorScheme.onSurface
    )
}

/**
 * Compact icon button for the timer bar
 */
@Composable
private fun CompactIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp) // Smaller than default
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp), // Smaller icon size
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}