package com.example.makart

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TopNavBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Explore Icon
        IconButton(
            onClick = { navController.navigate(Screen.Explore.route) }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_explore),
                contentDescription = "Explore"
            )
        }

        // Add Drawing Icon
        IconButton(
            onClick = { navController.navigate(Screen.DrawEditorNew.route) }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add_drawing),
                contentDescription = "Add Drawing"
            )
        }

        // Home Icon
        IconButton(
            onClick = { navController.navigate(Screen.MainMenu.route) }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home"
            )
        }

        // Logout Icon
        IconButton(
            onClick = {
                logoutFromServer {
                    FirebaseAuth.getInstance().signOut()
                    }
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) // Clear backstack to prevent returning to other screens after logout
                }
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = "Logout"
            )
        }
    }
}

private fun logoutFromServer(onLogoutComplete: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = ApiService.ktorAPIConnection.logout()
            if (response.isSuccessful) {
                Log.d("ServerResponse", "Logged out from server successfully.")
                onLogoutComplete()
            } else {
                Log.e("ServerResponse", "Error logging out from server: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Error logging out from server: ${e.message}")
        }
    }
}
