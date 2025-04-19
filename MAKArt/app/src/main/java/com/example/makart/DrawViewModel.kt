//package com.example.makart
//
//
//import android.app.Application
//import android.graphics.Bitmap
//import android.util.Base64
//import android.util.Log
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Canvas
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.PathEffect
//import androidx.compose.ui.graphics.StampedPathEffectStyle
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//
//
//
//// ViewModel class to manage and store the state of the drawing strokes and interact with the Room database.
//class DrawingViewModel(application: Application) : AndroidViewModel(application) {
//    //creating the api service initialization
//    private val apiServ = ApiService.ktorAPIConnection
//
//
//    // MutableStateFlow for stroke properties, holding the current properties for drawing (e.g., color, stroke width).
//    private val _currentPathProperty = MutableStateFlow(StrokeProperties())
//    // Expose the current path properties as a read-only StateFlow
//    val currentPathProperty: StateFlow<StrokeProperties> = _currentPathProperty.asStateFlow()
//
//    // List to store the lines (strokes) of the current drawing.
//    val lines = mutableStateListOf<Line>()
//
//    // Function to add a new line (stroke) to the list of lines in the current drawing.
//    fun addLine(line: Line) {
//        lines.add(line)
//    }
//
//    // Initialize DrawingDao to access the Room database for drawings.
//    // The database is initialized using the singleton pattern to avoid multiple instances.
//    private val drawingDao: DrawingDao = DrawingDatabase.getDatabase(application).drawingDao()
//
//    // Flow to observe the list of all drawings from the Room database.
//    val drawingList: Flow<List<DrawingEntity>> = drawingDao.getAllDrawings()
//
//    // Stores the drawing-entity info for the current drawing being accessed
//    private val _currentDrawing = MutableStateFlow<DrawingEntity?>(null)
//    // Expose the current drawing as a read-only StateFlow
//    val currentDrawing: StateFlow<DrawingEntity?> = _currentDrawing.asStateFlow()
//
//    // Init gson, obj used to convert Kotlin objects into JSON (serialization)
//    private val gson = Gson()
//
//    // Convert a list of Line objects to a JSON string
//    private fun linesToJson(lines: List<Line>): String {
//        return gson.toJson(lines)
//    }
//
//    // Convert a JSON string to a list of Line objects
//    private fun jsonToLines(json: String): List<Line> {
//        // Create an anonymous subclass of TypeToken that captures the specific type List<Line>.
//        val type = object : TypeToken<List<Line>>() {}.type
//        return gson.fromJson(json, type)
//    }
//
//    // Load a drawing and set the lines in the ViewModel
//    fun loadDrawing(id: Int) {
//        viewModelScope.launch {
//            val drawing = drawingDao.getDrawingById(id) // get drawing by ID from DB
//            _currentDrawing.value = drawing // set the current drawing to the one retrieved from DB
//            if (drawing != null) {
//                lines.clear()
//                lines.addAll(jsonToLines(drawing.lines)) // Load the lines from the JSON string
//            }
//        }
//    }
//
//    // Save drawing takes the info needed for drawing-entity and either adds a new drawing to DB
//    // or updated the current drawing
//    fun saveDrawing(name: String, thumbnailBitmap: Bitmap) {
//        viewModelScope.launch {
//            try {
//                val thumbnailBase64 = bitmapToBase64(thumbnailBitmap) // convert image to string
//                val lastModified = getCurrentDateTime()
//                val linesJson = linesToJson(lines) // Convert the lines to JSON
//                // Check if user is editing an existing drawing
//                if (_currentDrawing.value != null) {
//                    // Update the existing drawing
//                    drawingDao.updateDrawing(
//                        id = _currentDrawing.value!!.id,
//                        name = name,
//                        thumbnail = thumbnailBase64,
//                        lastModified = lastModified,
//                        lines = linesJson
//                    )
//                } else {
//                    // Insert a new drawing
//                    drawingDao.insertDrawing(
//                        DrawingEntity(
//                            name = name,
//                            thumbnail = thumbnailBase64,
//                            lastModified = lastModified,
//                            lines = linesJson
//                        )
//                    )
//                }
//            } catch (e: Exception) {
//                "Failed to save drawing"
//            }
//        }
//    }
//
//
//
//    // Function to delete a drawing by ID
//    fun deleteDrawing(id: Int) {
//        viewModelScope.launch {
//            try {
//                drawingDao.deleteDrawingById(id) // Delete drawing from the database
//            } catch (e: Exception) {
//                // Handle any potential errors during deletion
//                "Failed to delete drawing"
//            }
//        }
//    }
//
//}
//
//// Data class representing a single line (stroke) in the drawing.
//// Each line has a start and end point, a color, and a stroke width.
//data class Line(
//    val start: Offset,
//    val end: Offset,
//    val color: Color = Color.Black,
//    val strokeWidth: Dp = 1.dp,
//    val strokeCap: StrokeCap = StrokeCap.Round,
//    val strokeCapCustom: PathEffect? = null
//)
//
//// Data class representing the properties of the current stroke (e.g., color, width, stroke cap).
//data class StrokeProperties(
//    val color: Color = Color.Black,   // Default color is black.
//    val strokeWidth: Float = 5f,      // Default stroke width is 5.
//    val strokeCap: StrokeCap = StrokeCap.Round,  // Default stroke cap is rounded.
//    val strokeCapCustom: PathEffect? = null      // Add a custom PathEffect for the stroke cap
//)
//
