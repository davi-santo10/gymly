package br.santo.gymly.features.routines.ui.routinelist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.santo.gymly.ExerciseApplication
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(navController: NavController) {
  val application = LocalContext.current.applicationContext as ExerciseApplication

  val viewModel: RoutinesViewModel = viewModel(
    factory = RoutinesViewModelFactory(application.routinesRepository)
  )

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text("My Routines")
        },
        windowInsets = WindowInsets(0.dp)
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          navController.navigate(Screen.CreateRoutine.route)
        }
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add new routine"
        )
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier.padding(innerPadding)
    ) {
      items(uiState.routines) { routine ->
        RoutineItem(routine = routine)
      }
    }
    
  }
}

@Composable
fun RoutineItem (
  routine: Routine,
  modifier: Modifier = Modifier
) {
  Card (
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.FitnessCenter,
        contentDescription = "Routine train icon",
        modifier = Modifier.size(40.dp)
      )
    }
  }
}
