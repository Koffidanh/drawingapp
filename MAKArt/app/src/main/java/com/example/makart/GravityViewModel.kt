//package com.example.makart
//
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//
//class GravityViewModel : ViewModel() {
//    private val _xOffset = MutableLiveData(0f)
//    private val _yOffset = MutableLiveData(0f)
//    val xOffset: LiveData<Float> get() = _xOffset
//    val yOffset: LiveData<Float> get() = _yOffset
//
//    private val _ballPath = MutableLiveData<List<Line>>(emptyList())
//    val ballPath: LiveData<List<Line>> = _ballPath
//
//    fun updateOffsets(newX: Float, newY: Float) {
//        val scaleFactor = 50f
//        val screenWidth = 400
//        val screenHeight = 2000
//
//        _xOffset.value = (newX * scaleFactor).coerceIn(0f, screenWidth.toFloat() - 60.dp.value)
//        _yOffset.value = (-newY * scaleFactor).coerceIn(0f, screenHeight.toFloat() - 60.dp.value)
//    }
//
//}
//
//
