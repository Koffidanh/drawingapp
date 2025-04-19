package com.example.makart.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.makart.R
import com.example.makart.utility.StrokeProperties
import androidx.compose.runtime.*



@Composable
fun StrokeWidthComponent(
    currentStrokeProperty: StrokeProperties,
    updateCurrentStrokeProperty: (newStrokeWidth: Float?, strokeCap: StrokeCap?) -> Unit,
    onDismiss: () -> Unit
) {
    // State to track if the square shape is picked
    var isSquarePicked by remember { mutableStateOf(false) }
    var isRoundPicked by remember { mutableStateOf(false) }
    var isHeartPicked by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .width(350.dp)
            .padding(16.dp)
            .wrapContentHeight()
    ) {
        // Close button in the top-right corner
        IconButton(
            onClick = { onDismiss() },
            modifier = Modifier.align(Alignment.TopEnd) // Aligns the X to top-right
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close dialog",
                tint = Color.Black
            )
        }

        // Main Content
        Column(
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            // Stroke width slider with rounded value
            Text(
                text = "Adjust Stroke Width: ${"%.2f".format(currentStrokeProperty.strokeWidth)}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Slider(
                value = currentStrokeProperty.strokeWidth,
                onValueChange = {
                    updateCurrentStrokeProperty(it, null)
                },
                valueRange = 1f..50f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            // Row with shape buttons
            Row {
                //Square shape
                IconButton(
                    onClick = {
                        // Update the stroke cap to Square
                        updateCurrentStrokeProperty(currentStrokeProperty.strokeWidth, StrokeCap.Square)
                        // Set the state to true when square is picked
                        isSquarePicked = true
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.square_shape),
                        contentDescription = "Square Shape",
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Round shape
                IconButton(
                    onClick = {
                        // Update the stroke cap to Square
                        updateCurrentStrokeProperty(currentStrokeProperty.strokeWidth, StrokeCap.Round)
                        // Set the state to true when square is picked
                        isRoundPicked = true
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.circle_shape_new),
                        contentDescription = "Round Shape",
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Heart shape
//                IconButton(
//                    onClick = {
//                        // Update the stroke cap to Square
//                        updateCurrentStrokeProperty(currentStrokeProperty.strokeWidth, StrokeCap.CustomHeart)
//                        // Set the state to true when square is picked
//                        isHeartPicked = true
//                    },
//                    modifier = Modifier.size(40.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.heart_shape),
//                        contentDescription = "Heart Shape",
//                        modifier = Modifier.size(40.dp)
//                    )
//                }

            }

            // Conditionally display the text if square shape is picked
            if (isSquarePicked) {
                isRoundPicked = false
                isHeartPicked = false
                Text(
                    text = "Square Shape Picked!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )


            }

            // Conditionally display the text if round shape is picked
            if (isRoundPicked) {
                isSquarePicked = false
                isHeartPicked = false
                Text(
                    text = "Round Shape Picked!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

                // Conditionally display the text if heart shape is picked
//                if (isHeartPicked) {
//                    isSquarePicked = false
//                    isRoundPicked = false
//                    Text(
//                        text = "Heart Shape Picked!",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 24.dp),
//                        textAlign = TextAlign.Center,
//                        color = Color.Black,
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }

        }
    }
}


//@Composable
//fun MultiDashedLine(strokeProperties: StrokeProperties, color: Color, modifier: Modifier = Modifier) {
//    val density = LocalDensity.current
//    with(density) {
//        // Convert strokeWidth to Dp before converting to pixels
//        val strokeWidthDp = strokeProperties.strokeWidth.dp
//        val dashOnInterval1 = (strokeWidthDp * 4).toPx()
//        val dashOffInterval1 = (strokeWidthDp * 2).toPx()
//        val dashOnInterval2 = (strokeWidthDp / 4).toPx()
//        val dashOffInterval2 = (strokeWidthDp * 2).toPx()
//
//        val pathEffect = PathEffect.dashPathEffect(
//            intervals = floatArrayOf(dashOnInterval1, dashOffInterval1, dashOnInterval2, dashOffInterval2),
//            phase = 0f
//        )
//
//        // Draw the dashed line on the canvas
//        Canvas(modifier.size(200.dp, 40.dp)) {  // Set size of the Canvas if needed
//            drawLine(
//                color = color,
//                start = Offset(0f, 0f),
//                end = Offset(size.width, 0f),  // size is available inside this Canvas block
//                strokeWidth = strokeWidthDp.toPx(),
//                cap = StrokeCap.Round,
//                pathEffect = pathEffect
//            )
//        }
//    }
//}

// Function to stamp the heart
//@Composable
//fun HeartLine(strokeWidth: Float,color: Color, modifier: Modifier = Modifier) {
//    val shapeRadius = strokeWidth.dp / 2
//    val dotSpacing = shapeRadius * 4
//    val density = LocalDensity.current
//
//    val heartPath = remember {
//        with(density) {
//            Path().apply {
//                val width = (shapeRadius * 2).toPx()
//                val height = (shapeRadius * 2).toPx()
//                moveTo(width / 2, height / 4)
//                cubicTo(width / 4, 0f, 0f, height / 3, width / 4, height / 2)
//                lineTo(width / 2, height * 3 / 4)
//                lineTo(width * 3 / 4, height / 2)
//                cubicTo(width, height / 3, width * 3 / 4, 0f, width / 2, height / 4)
//            }
//        }
//    }
//    Canvas(modifier) {
//        val pathEffect = PathEffect.stampedPathEffect(
//            shape = heartPath,
//            advance = dotSpacing.toPx(),
//            phase = 0f,
//            style = StampedPathEffectStyle.Translate
//        )
//        drawLine(
//            color = color,
//            start = Offset(0f, 0f),
//            end = Offset(size.width, 0f),
//            pathEffect = pathEffect,
//            strokeWidth = shapeRadius.toPx()
//        )
//    }
//}





