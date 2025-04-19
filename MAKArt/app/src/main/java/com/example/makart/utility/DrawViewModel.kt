package com.example.makart.utility

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.makart.APIServ
import com.example.makart.ApiService
import com.example.makart.KtorAPIConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DrawingViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentPathProperty = MutableStateFlow(StrokeProperties())
    val currentPathProperty: StateFlow<StrokeProperties> = _currentPathProperty

    val lines = mutableStateListOf<Line>()

    // Set up the API service and Firebase
    private val _apiService = APIServ(ApiService.ktorAPIConnection)
    private val auth = FirebaseAuth.getInstance()
    private val firebaseId = auth.currentUser?.uid

    var currentDrawingId: Int? = null

    // Function to add a new line (stroke) to the current drawing
    fun addLine(line: Line) {
        lines.add(line)
    }

    // Function to fetch user drawings from the server
    suspend fun apiGetUserDrawings(): List<DrawingEntity> {
        val drawings = firebaseId?.let { uid -> _apiService.getUserDrawingsFromServer(uid) }
        return drawings?.map { convertToDrawingEntity(it) } ?: emptyList()
    }
    suspend fun fetchUserDrawings() {
        firebaseId?.let { uid ->
            val drawings = _apiService.getUserDrawingsFromServer(uid)?.map { convertToDrawingEntity(it) }
            _userDrawings.value = drawings ?: emptyList()
        }
    }

    // Load a drawing from the server
    fun apiLoadDrawing(drawingId: Int) {
        viewModelScope.launch {
            try {
                val drawingData = _apiService.loadDrawingFromServer(drawingId)
                if (drawingData != null) {
                    _currentDrawing.value = convertToDrawingEntity(drawingData)
                    lines.clear()
                    lines.addAll(jsonToLines(drawingData.lines))
                } else {
                    Log.e("APIError", "No drawing data found for ID: $drawingId")
                }
            } catch (e: Exception) {
                Log.e("APIError", "Failed to load drawing: ${e.message}")
            }
        }
    }

    fun loadSharedDrawing(drawingId: Int) {
        viewModelScope.launch {
            try {
                val drawingData = _apiService.loadDrawingFromServer(drawingId)
                if (drawingData != null) {
                    _currentDrawing.value = null
                    lines.clear()
                    lines.addAll(jsonToLines(drawingData.lines))
                } else {
                    Log.e("APIError", "No drawing data found for ID: $drawingId")
                }
            } catch (e: Exception) {
                Log.e("APIError", "Failed to load drawing: ${e.message}")
            }
        }
    }

    // Store the current drawing being accessed
    private val _currentDrawing = MutableStateFlow<DrawingEntity?>(null)
    val currentDrawing: StateFlow<DrawingEntity?> = _currentDrawing

    private val gson = Gson()

    private fun linesToJson(lines: List<Line>): String {
        return gson.toJson(lines)
    }

    private fun jsonToLines(json: String): List<Line> {
        val type = object : TypeToken<List<Line>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun convertToDrawingEntity(drawingData: KtorAPIConnection.DrawingData): DrawingEntity {
        return DrawingEntity(
            drawingId = drawingData.drawingId,
            name = drawingData.name,
            thumbnail = drawingData.thumbnail,
            lastModified = drawingData.lastModified,
            lines = drawingData.lines,
            ownerId = drawingData.ownerId,
            isShared = drawingData.isShared
        )
    }

    fun saveDrawing(drawingId: Int? = currentDrawingId, name: String, thumbnailBitmap: Bitmap, ownerId: String, isShared: Boolean) {
        viewModelScope.launch {
            try {
                val thumbnailBase64 = bitmapToBase64(thumbnailBitmap)
                val lastModified = getCurrentDateTime()
                val linesJson = linesToJson(lines)

                val drawingData = KtorAPIConnection.DrawingData(
                    drawingId = drawingId ?: -1,
                    name = name,
                    thumbnail = thumbnailBase64,
                    lastModified = lastModified,
                    lines = linesJson,
                    ownerId = ownerId,
                    isShared = isShared
                )

                CoroutineScope(Dispatchers.IO).launch {
                    if (_currentDrawing.value != null) {
                        _apiService.updateDrawingServer(drawingData.drawingId, drawingData)
                    } else {
                        _apiService.sendDrawingToServer(drawingData)
                    }
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Failed to save drawing: ${e.message}")
            }
        }
    }

    fun deleteDrawingAPI(drawingId: Int) {
        viewModelScope.launch {
            try {
                _apiService.deleteDrawingFromServer(drawingId)
                fetchUserDrawings() // Refresh the list after deletion
                if (currentDrawingId == drawingId) {
                    currentDrawingId = null
                }
            } catch (e: Exception) {
                Log.e("DeleteDrawingException", "Error deleting drawing: ${e.message}")
            }
        }
    }

    fun updateDrawingShareStatus(drawingId: Int, isShared: Boolean) {
        viewModelScope.launch {
            _apiService.updateDrawingShareStatusServer(drawingId, isShared)
        }
    }
    // Fetch shared drawings
    suspend fun getSharedDrawings(): List<DrawingEntity> {
        val sharedDrawings = _apiService.fetchSharedDrawingsServer()
        return if (sharedDrawings != null) {
            sharedDrawings.map { drawingData ->
                DrawingEntity(
                    drawingId = drawingData.drawingId,
                    name = drawingData.name,
                    thumbnail = drawingData.thumbnail,
                    lastModified = drawingData.lastModified,
                    lines = drawingData.lines,
                    ownerId = drawingData.ownerId,
                    isShared = drawingData.isShared
                )
            }
        } else {
            emptyList() // Return an empty list if no drawings were fetched
        }
    }

    private val _userDrawings = MutableStateFlow<List<DrawingEntity>>(emptyList())
    val userDrawings: StateFlow<List<DrawingEntity>> = _userDrawings

}

// Utility data classes

data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 1.dp,
    val strokeCap: StrokeCap = StrokeCap.Round,
    val strokeCapCustom: PathEffect? = null
)

data class StrokeProperties(
    val color: Color = Color.Black,
    val strokeWidth: Float = 5f,
    val strokeCap: StrokeCap = StrokeCap.Round,
    val strokeCapCustom: PathEffect? = null
)

data class DrawingEntity(
    val drawingId: Int,
    val name: String,
    val thumbnail: String?,
    val lastModified: String,
    val lines: String,
    val ownerId: String,
    val isShared: Boolean
)
