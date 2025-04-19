package com.example.makart.utility

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Converts an ImageBitmap to a standard Android Bitmap
fun imageBitmapToBitmap(imageBitmap: ImageBitmap): Bitmap {
    return imageBitmap.asAndroidBitmap()
}

// Function to encode a Bitmap as a Base64 string. Used for storing images in the DB in string format.
fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

// Function to decode a Base64-encoded string back into a Bitmap.
// This is used when retrieving a drawing thumbnail from the DB.
fun base64ToBitmap(base64String: String): Bitmap {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

// Function to get the current date and time as a formatted string. Used for tracking when a drawing was last modified.
fun getCurrentDateTime(): String {
    // Creates a SimpleDateFormat with the desired pattern and locale
    val sdf = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault())
    // Formats the current date and time into a string
    return sdf.format(Date())
}


// Helper function to save the bitmap and get its URI
fun createURIFromBitmap(context: Context, bitmap: Bitmap): Uri {
    // Accesses the cache directory where temporary files can be stored.
    val cacheDir = context.cacheDir
    // Creates a temporary .jpg file in the cache directory.
    val imageFile = File.createTempFile("drawing_", ".jpg", cacheDir)

    // Open a file output stream to write to the newly created image file
    val outputStream = FileOutputStream(imageFile)
    // Compress the bitmap and write it to the file as a JPEG
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    //Return a content Uri for the image file, which is how it's shared with other apps
    return FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
}