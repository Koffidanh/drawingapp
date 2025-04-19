package com.example.makart

//import GravityViewModel
import ShakeDetector
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.makart.dialog.ColorPickerComponent
import com.example.makart.dialog.SaveDrawingDialog
import com.example.makart.dialog.StrokeWidthComponent
import com.example.makart.utility.DrawingViewModel
import com.example.makart.utility.GravityViewModel
import com.example.makart.utility.Line
import com.example.makart.utility.StrokeProperties
import com.example.makart.utility.createURIFromBitmap
import com.example.makart.utility.imageBitmapToBitmap
import com.example.makart.utils.SensorUtils
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.google.firebase.auth.FirebaseAuth
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun DrawEditorScreen(
    drawingId: Int?, // passed when selecting drawing from drawing list
    drawingViewModel: DrawingViewModel = viewModel(), // ViewModel for managing the drawing state
    gravityViewModel: GravityViewModel = viewModel(), //ViewModel for gravity and ball rolling
    onBack: () -> Unit, // Callback for navigating back
    isImported: Boolean = false, // Flag to indicate if the drawing is imported,
    userId:String
) {
    //Check if there is a drawing-id, if so load drawing info (retrieves drawing-entity and drawing lines)
    LaunchedEffect(drawingId, isImported) {
        if (drawingId != null) {
            if (isImported) {
                drawingViewModel.loadSharedDrawing(drawingId)
            } else {
                drawingViewModel.apiLoadDrawing(drawingId)
            }
        }
    }

    // Using https://github.com/PatilShreyas/Capturable lib to grab capture screen shot of drawing
    val captureController = rememberCaptureController()
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    // Get the list of lines and current stroke properties from the ViewModel
    val lines = drawingViewModel.lines
    val currentStrokeProperties by drawingViewModel.currentPathProperty.collectAsState() //convert StateFlow to composable state

    // State variables to manage current color, stroke width, gravity, erase toggle state
    var selectedColor by remember { mutableStateOf(currentStrokeProperties.color) } // Current selected color
    var strokeWidth by remember { mutableStateOf(currentStrokeProperties.strokeWidth) } // Current stroke width
    var hexColorString by remember { mutableStateOf("#000000") } // Color in hex format
    var eraseMode by remember { mutableStateOf(false) }
    var gravityMode by remember { mutableStateOf(false) }
    var heartMode by remember { mutableStateOf(false) }

    // State variables to control visibility of dialogs
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var showStrokeWidthDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }


    val colorPickerController = remember { ColorPickerController() }

    // Current stroke property (color, stroke width, etc.)
    var currentStrokeProperty by remember {
        mutableStateOf(StrokeProperties(color = selectedColor, strokeWidth = strokeWidth))
    }

    //getting and managing the gravity sesnors::
    val sensorManager = LocalContext.current.getSystemService(SensorManager::class.java) //sensors for gravity and ball rolling
    val gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
    val sensorUtils = remember { SensorUtils() }
    val gravityData by sensorUtils.getGravityData(gravitySensor, sensorManager).collectAsState(initial = floatArrayOf(0f, 0f, 0f))
    gravityViewModel.updateOffsets(gravityData[0], gravityData[1])
    // Observe offsets for ball movement
    val xOffset by gravityViewModel.xOffset.observeAsState(0f)
    val yOffset by gravityViewModel.yOffset.observeAsState(0f)
    // Previous position of the ball
    var previousPosition = remember { Offset(xOffset, yOffset) }


    //shake testing
    val context = LocalContext.current
    val shakeDetector = remember {ShakeDetector(context){
        if(!gravityMode) {
            if(drawingViewModel.lines.isNotEmpty()) {
                drawingViewModel.lines.clear()
                Toast.makeText(context, "Shaked and erased", Toast.LENGTH_SHORT).show()
            }

        }
    } }
    DisposableEffect(Unit) {
        shakeDetector.start()
        onDispose {
            shakeDetector.stop()
        }
    }


    // Main layout for the DrawEditorScreen
    Column(
        modifier = Modifier.fillMaxSize() // Fill the entire screen
    ) {
        // Top nav-bar with buttons for Back, Save, Color, Stroke, Gravity, Erase
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back"
                )
            }
            IconButton(onClick = { showSaveDialog = true
//                  // Fetch your DrawingEntity instance
//                val apiService = APIServ(ApiService.ktorAPIConnection)
//                CoroutineScope(Dispatchers.IO).launch {
//                    apiService.sendDrawingToServer(drawingEntity)
//                }

            }) {
                Icon(
                    painterResource(id = R.drawable.ic_floppy_disk),
                    contentDescription = "Save"
                )
            }
            IconButton(onClick = {
                onShareClick(scope, context, captureController)
            }) {
                Icon(
                    painterResource(id = R.drawable.ic_send_to_other_devices),
                    contentDescription = "Share"
                )
            }
            IconButton(onClick = { showColorPickerDialog = true }) {
                Icon(
                    painterResource(id = R.drawable.palette),
                    contentDescription = "Color"
                )
            }
            IconButton(onClick = { showStrokeWidthDialog = true }) {
                Icon(
                    painterResource(id = R.drawable.ic_paint_brush),
                    contentDescription = "Stroke"
                )
            }
            IconButton(onClick = {
                gravityMode = !gravityMode
            }){
                Icon(
                    painterResource(id = if (gravityMode) R.drawable.baseline_imagesearch_roller_24 else R.drawable.ball_gravity_test),
                    contentDescription = "Gravity"

                )
            }
            IconButton(onClick = {
                eraseMode = !eraseMode // Toggle erase mode on/off
            }) {
                Icon(
                    painterResource(id = if (eraseMode) R.drawable.ic_eraser_off else R.drawable.ic_eraser),
                    contentDescription = "Erase"
                )
            }
        }

        //Capture everything in this column for 'Capturable'
        Column(modifier = Modifier.capturable(captureController)) {
            // Drawing Canvas where the user can draw
            Canvas(
                modifier = Modifier
                    .fillMaxSize() // Fill the screen space
                    .pointerInput(true) {
                        detectDragGestures { change, dragAmount ->
                            change.consume() // Consume the pointer input

                            // Determine whether to use the selected color or erase (white color)
                            val colorToUse = if (eraseMode) Color.White else selectedColor
                            val widthToUse = if (eraseMode) 20.dp else strokeWidth.dp

                            // Create a new line based on the drag gesture
                            val line = Line(
                                start = change.position - dragAmount,
                                end = change.position,
                                color = colorToUse,
                                strokeWidth = widthToUse,
                                strokeCap = currentStrokeProperty.strokeCap
                            )

                            // Add the new line to the ViewModel
                            drawingViewModel.addLine(line)
                        }
                    }
            ) {
                drawRect(
                    color = Color.White, // Set the background to white
                    size = size
                )
                // Draw the ball if gravity mode is active
                if (gravityMode) {
                    // Initialize or reset previousPosition
                    if (previousPosition == Offset.Unspecified) {
                        previousPosition = Offset(xOffset.dp.toPx(), yOffset.dp.toPx())
                    }

                    // Draw the ball
                    drawCircle(
                        color = Color.Green,
                        radius = 20.dp.toPx(),
                        center = Offset(xOffset.dp.toPx(), yOffset.dp.toPx())
                    )

                    // Determine the color and width to use (for drawing or erasing)
                    val colorToUse = if (eraseMode) Color.White else selectedColor
                    val widthToUse = if (eraseMode) 20.dp else strokeWidth.dp

                    // Calculate the current position
                    val currentPosition = Offset(xOffset.dp.toPx(), yOffset.dp.toPx())

                    // Add new line if position has changed
                    if (previousPosition != currentPosition) {
                        val line = Line(
                            start = currentPosition ,  // Starting at the previous position
                            end = currentPosition + previousPosition,     // Ending at the current position
                            color = colorToUse,        // Line color or erase color
                            strokeWidth = widthToUse,  // Stroke width in dp
                            strokeCap = currentStrokeProperty.strokeCap
                        )

                        // Add the line to the ViewModel
                        drawingViewModel.addLine(line)

                        // Update the previous position
                        previousPosition = currentPosition
                    }
                }

                // Draw all the lines stored in the ViewModel
                lines.forEach { line ->
                    drawLine(
                        color = line.color,       // Color of the line
                        start = line.start,       // Start point of the line
                        end = line.end,           // End point of the line
                        strokeWidth = line.strokeWidth.toPx(),  // Convert stroke width to px
                        cap = line.strokeCap      // StrokeCap for the line
                    )
                }
            }
        }
    }

    // Show color picker dialog if the state is true
    if (showColorPickerDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)) // Semi-transparent background for modal effect
                .padding(32.dp)
                .testTag("ColorPickerDialog"),
            contentAlignment = Alignment.Center
        ) {
            ColorPickerComponent(
                currentColor = selectedColor,
                updateHexColorCode = { newHex -> hexColorString = newHex }, // Update hex color code
                updateCurrentColor = { newColor -> selectedColor = newColor }, // Update selected color
                controller = colorPickerController,
                onDismiss = { showColorPickerDialog = false } // Close the dialog
            )
        }
    }

    // Show stroke width dialog if the state is true
    if (showStrokeWidthDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)) // Semi-transparent background for modal effect
                .padding(32.dp)
                .testTag("StrokePickerDialog"),
            contentAlignment = Alignment.Center
        ) {
            StrokeWidthComponent(
                currentStrokeProperty = currentStrokeProperty,
                updateCurrentStrokeProperty = { newStrokeWidth, strokeCap ->
                    newStrokeWidth?.let { strokeWidth = it } // Update stroke width
                    currentStrokeProperty = currentStrokeProperty.copy(
                        strokeWidth = newStrokeWidth ?: currentStrokeProperty.strokeWidth,
                        strokeCap = strokeCap ?: currentStrokeProperty.strokeCap,
                    )
                },
                onDismiss = { showStrokeWidthDialog = false } // Close the dialog
            )
        }
    }

    // Show save dialog if the state is true
    if (showSaveDialog) {
        SaveDrawingDialog(
            //Check if there is a name connected to this drawing
            initialName = drawingViewModel.currentDrawing.value?.name.orEmpty(),
            onConfirm = { drawingName ->
                // When the user confirms, save the drawing with the provided name
                showSaveDialog = false

                val firebaseUid = auth.currentUser?.uid

                scope.launch {
                    val bitmapAsync = captureController.captureAsync()
                    try {
                        val imageBitmap = bitmapAsync.await()  // Capture the ImageBitmap
                        val bitmap = imageBitmapToBitmap(imageBitmap)  // Convert ImageBitmap to Bitmap

                        if (firebaseUid != null) {
                            drawingViewModel.saveDrawing(name = drawingName, thumbnailBitmap = bitmap, ownerId =firebaseUid, isShared = false )
                        } // Save drawing
                    } catch (error: Throwable) {
                        Log.e("CaptureError", "Error capturing drawing: $error")
                    }
                }
            },
            onDismiss = { showSaveDialog = false } // Close the dialog
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDrawEditorScreen() {
    DrawEditorScreen(
        drawingId = 1,
        onBack = { /* No action needed for preview */ },
        userId =""
    )
}

@OptIn(ExperimentalComposeApi::class)
fun onShareClick(
    scope: CoroutineScope,
    context: Context,
    captureController: CaptureController
) {
    scope.launch {
        val bitmapAsync = captureController.captureAsync()
        try {
            val imageBitmap = bitmapAsync.await()  // Capture the ImageBitmap
            val bitmap = imageBitmapToBitmap(imageBitmap)  // Convert ImageBitmap to Bitmap
            bitmap?.let {
                // save the Bitmap to a file and get a sharable Uri
                val uri = createURIFromBitmap(context, it)
                //Configure a share-intent by adding the image uri to intent,
                // specifying to other apps the file format is JPEG and they have read permission.
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/jpeg"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                // Start the share intent, allowing the user to choose an app to share the drawing with
                context.startActivity(Intent.createChooser(shareIntent, "Share Drawing"))
            }

        } catch (error: Throwable) {
            Log.e("CaptureError", "Error capturing drawing: $error")
        }
    }
}



