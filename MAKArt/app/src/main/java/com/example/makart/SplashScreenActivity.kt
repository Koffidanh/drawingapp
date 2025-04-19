package com.example.makart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit = {}) {
    var alphaValue by remember { mutableStateOf(0f) }  // Controls text opacity
    var scaleValue by remember { mutableStateOf(0.8f) }  // Controls text size (scale)

    // Animate the alpha and scale values
    val animatedAlpha by animateFloatAsState(
        targetValue = alphaValue,
        animationSpec = tween(durationMillis = 1500) // Duration for fade in
    )

    val animatedScale by animateFloatAsState(
        targetValue = scaleValue,
        animationSpec = tween(durationMillis = 1500) // Duration for size increase
    )

    LaunchedEffect(true) {
        delay(1000)  // Delay before starting the text animation (1 second delay)
        alphaValue = 1f  // Fade in text
        scaleValue = 1f  // Scale up text slightly
        delay(3000)  // Keep the splash screen for an additional 3 seconds
        onNavigateToLogin()  // Navigate to login screen after splash
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.paint_logo),
            contentDescription = "Art Image",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Apply fade-in and scale-up animation to the text
        Text(
            text = "M.A.K Art",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .graphicsLayer(
                    alpha = animatedAlpha,  // Fade-in effect
                    scaleX = animatedScale, // Scale the text horizontally
                    scaleY = animatedScale  // Scale the text vertically
                )
        )
    }
}



