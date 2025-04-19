package com.example.makart.dialog


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker

@Composable
fun ColorPickerComponent(
    currentColor: Color,
    updateHexColorCode: (String) -> Unit,
    updateCurrentColor: (Color) -> Unit,
    controller: ColorPickerController,
    onDismiss: () -> Unit
) {
    var isInitialized by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(350.dp)
            .padding(16.dp)
            .wrapContentHeight()
            .testTag("ColorPickerComponent")
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
            // HSV Color Picker
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp),
                controller = controller,
                onColorChanged = {
                    if (!isInitialized) {
                        isInitialized = true
                    } else {
                        updateHexColorCode(it.hexCode)
                        updateCurrentColor(it.color)
                    }
                },
                initialColor = currentColor
            )

            // Alpha Slider
            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(20.dp),
                controller = controller,
                tileOddColor = Color.White,
                tileEvenColor = Color.Black
            )

            // Brightness Slider
            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(20.dp),
                controller = controller,
            )

        }
    }
}
