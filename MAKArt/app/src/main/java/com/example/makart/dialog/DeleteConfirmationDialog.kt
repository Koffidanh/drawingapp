package com.example.makart.dialog

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
@Composable
fun DeleteConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() }, // Close dialog on outside click or back button
            title = {
                Text(text = "Delete Drawing", color = MaterialTheme.colorScheme.onSurface) // Dialog title in default text color
            },
            text = {
                Text(
                    "Are you sure you want to delete this drawing?",
                    color = MaterialTheme.colorScheme.onSurface // Dialog text in default text color
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmDelete() // Trigger the delete action
                        onDismiss() // Close the dialog after confirming
                    }
                ) {
                    Text(
                        "Delete",
                        color = Color.Red // Make the delete button red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() } // Close the dialog without deleting
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.primary // Cancel button in primary color
                    )
                }
            },
            containerColor = Color.White, // Make the dialog background white
            tonalElevation = 8.dp, // Optional: Adds depth to the dialog by elevating it
            shape = MaterialTheme.shapes.medium // Optional: Adjust shape (e.g., rounded corners)
        )
    }
}
