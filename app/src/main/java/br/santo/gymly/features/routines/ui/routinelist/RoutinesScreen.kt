package br.santo.gymly.features.routines.ui.routinelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import br.santo.gymly.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
  navController: NavController,
  viewModel: RoutinesViewModel = hiltViewModel()
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  var isNavigating by remember { mutableStateOf(false) }
  
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
      // Use AnimatedVisibility to smoothly hide the FAB
      AnimatedVisibility(
        visible = !isNavigating,
        exit = fadeOut(animationSpec = tween(100)) +
                scaleOut(animationSpec = tween(100))
      ) {
        FloatingActionButton(
          onClick = {
            isNavigating = true
            navController.navigate(Screen.CreateRoutine.route)
          }
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add new routine"
          )
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier.padding(innerPadding)
    ) {
      items(uiState.routines) { routine ->
        RoutineItem(
          routine = routine,
          onClick = {
            isNavigating = true
            navController.navigate(Screen.RoutineDetails.createRoute(routine.id))
        }
      )
      }
    }
    
  }
}

