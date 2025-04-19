package com.example.makart

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.makart.utility.DrawingEntity
import com.example.makart.utility.DrawingViewModel


@Composable
fun MainMenuScreen(navController: NavController, userId: String,drawingViewModel: DrawingViewModel = viewModel()) {

    val userDrawings by drawingViewModel.userDrawings.collectAsState()

    // Fetch user drawings when the screen is launched
    LaunchedEffect(Unit) {
        drawingViewModel.fetchUserDrawings()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Bar
        TopNavBar(navController = navController)

        Text("My Artwork")

        Spacer(modifier = Modifier.height(16.dp))

        // Display current users drawings from the Room database
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // For each drawing entity in the drawingList, display a DrawingListItem
            items(userDrawings) { drawingEntity ->
                DrawingListItem(
                    drawingEntity = drawingEntity,
                    onClick = {
                        // Navigate to the DrawEditorScreen with the drawing ID
                        navController.navigate(Screen.DrawEditor.createRoute(drawingEntity.drawingId))
                    },
                    onShareToggle = {
                        val newShareStatus = !drawingEntity.isShared
                        drawingViewModel.updateDrawingShareStatus(drawingEntity.drawingId, newShareStatus)
                    },
                    mode = CardMode.MyArt // Set to MyArt mode
                )
            }

        }
    }
}

