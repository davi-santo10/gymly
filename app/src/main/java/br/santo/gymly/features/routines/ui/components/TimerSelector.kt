package br.santo.gymly.features.routines.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimerSelectionSheet(
    currentRestTimeSeconds: Int,
    onTimeSelected: (seconds: Int) -> Unit
) {
    val presetTimes = listOf(30, 60, 90, 120, 150, 180, 240, 300)

    var customMinutes by rememberSaveable { mutableStateOf("") }
    var customSeconds by rememberSaveable { mutableStateOf("") }

    val isCustomTimeValid = (customMinutes.toIntOrNull() ?: 0) > 0 || (customSeconds.toIntOrNull() ?: 0) > 0

    fun formatSecondsToTimer(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return when {
            minutes > 0 && remainingSeconds > 0 -> "${minutes}m ${remainingSeconds}s"
            minutes > 0 -> "${minutes}m"
            else -> "${seconds}s"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Select Rest Time",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Current: ${formatSecondsToTimer(currentRestTimeSeconds)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 4
        ) {
            presetTimes.forEach { seconds ->
                val isSelected = currentRestTimeSeconds == seconds

                val buttonColors = if (isSelected) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }

                Button(
                    onClick = { onTimeSelected(seconds) },
                    colors = buttonColors,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = formatSecondsToTimer(seconds), fontSize = 12.sp)
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 20.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        Text(
            text = "Custom Time",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customMinutes,
                onValueChange = { customMinutes = it },
                label = { Text("Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(90.dp)
            )
            Text(":", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = customSeconds,
                onValueChange = { customSeconds = it },
                label = { Text("Sec") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val totalSeconds = (customMinutes.toIntOrNull() ?: 0) * 60 + (customSeconds.toIntOrNull() ?: 0)
                onTimeSelected(totalSeconds)
            },
            enabled = isCustomTimeValid,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Set Custom Time")
        }
    }
}