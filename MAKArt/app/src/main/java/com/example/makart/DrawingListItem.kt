package com.example.makart

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.makart.dialog.DeleteConfirmationDialog
import com.example.makart.utility.DrawingEntity
import com.example.makart.utility.DrawingViewModel
import com.example.makart.utility.base64ToBitmap
import com.google.firebase.firestore.FirebaseFirestore

// Enum to represent the card mode
sealed class CardMode {
    object MyArt : CardMode() // For MainMenuScreen
    object OthersArt : CardMode() // For ExploreScreen
}

//Uses the drawing data stored in drawingEntity to display the drawing-list-item
@Composable
fun DrawingListItem(
    drawingEntity: DrawingEntity,
    onClick: () -> Unit,
    onShareToggle: () -> Unit,
    mode: CardMode,
    drawingViewModel: DrawingViewModel = viewModel()
) {
    // [MyArt] State to track if the delete confirmation dialog is shown
    var showDialog by remember { mutableStateOf(false) }

    // [MyArt] State to toggle the share icon
    var isShared by remember { mutableStateOf(drawingEntity.isShared) }

    var username by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(drawingEntity.ownerId) {
        val db = FirebaseFirestore.getInstance()
        Log.d("CHECK", "Fetched user drawings: ${drawingEntity.ownerId}")
        db.collection("users")
            .whereEqualTo("uid", drawingEntity.ownerId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val data = document.data
                    username = data["username"] as? String // Set the username to the retrieved value
                }
            }
            .addOnFailureListener { exception ->
                Log.w("HELLO", "Error getting documents: ", exception)
            }
    }



    ElevatedCard(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .let {
                if (mode is CardMode.MyArt) it.clickable { onClick() } else it
            }, // [MyArt] Make clickable only if in MyArt mode
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
    ) {
        Column {
            // [OthersArt] Top row for avatar and username
            if (mode is CardMode.OthersArt) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_avitar),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = username ?: "Loading...", style = MaterialTheme.typography.bodyMedium)
                }
            }

            drawingEntity.thumbnail?.let { base64String ->
                val bitmap = base64ToBitmap(base64String)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(top = 8.dp)
                )
            }

            // Row for the title, timestamp, and action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Column for the title and timestamp on the bottom left
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = drawingEntity.name,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis // Add ellipsis if the text is too long
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = drawingEntity.lastModified,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Row for the action buttons on the bottom right
                Row {
                    when (mode) {
                        // [MyArt]
                        is CardMode.MyArt -> {
                            // Share/Unshare button
                            IconButton(onClick = {
                                isShared = !isShared
                                onShareToggle()
                                drawingViewModel.updateDrawingShareStatus(drawingEntity.drawingId, isShared)
                            }) {
                                val icon = if (isShared) R.drawable.ic_share else R.drawable.ic_share_off
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = if (isShared) "Unshare" else "Share",
                                    tint = Color.DarkGray
                                )
                            }
                            // Delete button
                            IconButton(
                                onClick = { showDialog = true }, // Show the delete confirmation dialog
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = "Delete",
                                    tint = Color.DarkGray
                                )
                            }
                        }
                        // [OthersArt]
                        is CardMode.OthersArt -> {
                            IconButton(
                                onClick = { onClick() },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_add_drawing),
                                    contentDescription = "Add Drawing",
                                    tint = Color.DarkGray
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    // [MyArt] Display the delete confirmation dialog
    if (mode is CardMode.MyArt) {
        DeleteConfirmationDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },

            onConfirmDelete = {
                drawingViewModel.deleteDrawingAPI(drawingEntity.drawingId)// todo testing this here
                showDialog = false
            }
        )
    }
}

