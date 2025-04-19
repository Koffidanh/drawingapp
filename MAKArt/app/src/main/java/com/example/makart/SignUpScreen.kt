package com.example.makart

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.makart.utility.DrawingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    drawingViewModel: DrawingViewModel = viewModel()
) {
    // Initialize Firebase Auth
    val auth = FirebaseAuth.getInstance()

    // State variables for the input fields
    val email = remember { mutableStateOf("easy@yahoo.com") }
    val username = remember { mutableStateOf("fartArt69") }
    val password = remember { mutableStateOf("Test123!") }

    // Get Context
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.paint_logo),
            contentDescription = "Art Image",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Sign Up", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally // Center the elements inside this Column
        ) {
            // Username input field
            TextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Username") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email input field
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password input field
            PasswordTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = "Password"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    auth.createUserWithEmailAndPassword(email.value, password.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Account created successfully
                                val firebaseUser = task.result?.user
                                if (firebaseUser != null) {
                                    val userId = firebaseUser.uid

                                    val user = User(userId, username.value) // Replace "username" with the actual username

                                    val db = FirebaseFirestore.getInstance()
                                    val usersRef = db.collection("users")

                                    usersRef.add(user)
                                        .addOnSuccessListener {
                                            onSignUpSuccess()
                                            Toast.makeText(
                                                context,
                                                "Account created successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Username is already taken.",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                }
                            } else {
                                // Handle errors
                                Toast.makeText(
                                    context,
                                    "Failed to create account: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFf9d95d)
                )
            ) {
                Text(text = "Sign Up")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignUpScreen(
        onSignUpSuccess = {},
    )
}

data class User(
    val uid: String = "",     // Firebase UID (default empty for Firestore compatibility)
    val username: String = ""  // Username
)