package com.example.makart

import android.graphics.Bitmap
import android.graphics.Color

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Base64
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.makart.utility.DrawingEntity
import com.example.makart.utility.DrawingViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@Composable
fun ExploreScreen(navController: NavController, drawingViewModel: DrawingViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    var sharedDrawings by remember { mutableStateOf(emptyList<DrawingEntity>()) }

    // Fetch shared drawings when ExploreScreen is first composed
    LaunchedEffect(Unit) {
        scope.launch {
            sharedDrawings = drawingViewModel.getSharedDrawings() // Call suspend function here
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopNavBar(navController = navController)

        Text("Explore Artworks")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // For each drawing entity, display a DrawingListItem
            items(sharedDrawings) { drawingEntity ->
                DrawingListItem(
                    drawingEntity = drawingEntity,
                    onClick = {
                        navController.navigate(Screen.DrawEditorImport.createRoute(drawingEntity.drawingId))
                    },
                    mode = CardMode.OthersArt,
                    onShareToggle = {}
                )
            }
        }
    }
}

