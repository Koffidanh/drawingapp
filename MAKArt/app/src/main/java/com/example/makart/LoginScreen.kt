package com.example.makart

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    // Initialize Firebase Auth
    val auth = FirebaseAuth.getInstance()

    //fakeaiden@gmail.com
    // State to hold the username and password inputs
    val email = remember { mutableStateOf("easy@yahoo.com") } // user fill username
    val password = remember { mutableStateOf("Test123!") } // user fill password

    // States for validation errors
    val emailError = remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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

        Text(text = "Login", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            // Email input field
            TextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                    emailError.value = if (!Patterns.EMAIL_ADDRESS.matcher(it).matches()) "Invalid email address" else null
                },
                label = { Text("Email") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = if (emailError.value != null) Color.Red else Color.Black,
                    unfocusedIndicatorColor = if (emailError.value != null) Color.Red else Color.Gray,
                    focusedLabelColor = if (emailError.value != null) Color.Red else Color.Black,
                    unfocusedLabelColor = if (emailError.value != null) Color.Red else Color.Black,
                ),
                isError = emailError.value != null
            )
            if (emailError.value != null) {
                Text(
                    text = emailError.value!!,
                    color = Color.Red,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Password input field
        PasswordTextField(
            value = password.value,
            onValueChange = {
                password.value = it
                passwordError = null // Reset error when typing
            },
            label = "Password",
            isError = passwordError != null, // Show error state when there's an error
            errorMessage = passwordError // Display the error message if present
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = {
                // Input validation
                if (email.value.isEmpty()) {
                    emailError.value = "Email cannot be empty"
                }
                if (password.value.isEmpty()) {
                    passwordError = "Password cannot be empty"
                }

                // Proceed if no errors
                if (emailError.value == null && passwordError == null) {
                    // Firebase Auth sign in
                    auth.signInWithEmailAndPassword(email.value, password.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUid = auth.currentUser?.uid
                                if (firebaseUid != null) {
                                    Log.d("LoginScreen", "Firebase UID: $firebaseUid")

                                // Send UID to the server
                                    val apiService = APIServ(ApiService.ktorAPIConnection)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        apiService.sendUidToServer(firebaseUid)
                                    }
                                }
                                else{
                                    Log.e("LoginScreen", "firebase uid is null")
                                }

                                onLoginSuccess()
                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                            } else {
                                // Login failed, show error message
                                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFf9d95d)
            )
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Sign-Up screen text
        val annotatedText = buildAnnotatedString {
            append("Don't have an account? ")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF8e5452),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Sign Up")
            }
        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                if (offset >= "Don't have an account? ".length) {
                    onNavigateToSignUp() // Trigger the sign-up navigation only when 'Sign Up' is clicked
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        onNavigateToSignUp = {},
        onLoginSuccess = {}
    )
}
