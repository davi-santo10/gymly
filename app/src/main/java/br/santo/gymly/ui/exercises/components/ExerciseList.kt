package br.santo.gymly.ui.exercises.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.santo.gymly.data.Exercise
import br.santo.gymly.data.MuscleGroup
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExercisesList(
    groupedExercises: Map<MuscleGroup, List<Exercise>>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding // Use padding from the Scaffold
    ) {
        groupedExercises.forEach { (muscleGroup, exercisesInGroup) ->
            stickyHeader {

                val formattedTitle = muscleGroup.name
                    .replace('_', ' ')
                    .lowercase(Locale.getDefault())
                    .split(' ')
                    .joinToString(" ") { it.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
                    }}

                MuscleGroupHeader(title = formattedTitle)
            }

            items(items = exercisesInGroup) { exercise ->
                ExerciseRow(exercise = exercise)

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}