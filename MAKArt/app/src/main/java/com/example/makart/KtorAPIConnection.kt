package com.example.makart

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.PUT

object ApiService {
    private const val BASE_URL = "http://10.0.2.2:6060/"

    val ktorAPIConnection: KtorAPIConnection by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KtorAPIConnection::class.java)
    }

//    val auth = FirebaseAuth.getInstance()
    var firebaseAuth: String? = null


}

// Interface to define Retrofit API endpoints
interface KtorAPIConnection {

    //todo currently unused register endpoint
    //right now when a user registers, it just sends their info to firebase
//    @POST("register")
//    suspend fun registerUser(@Body user: UserRegistration): Response<Unit>

    @POST("users/login")
    suspend fun sendUid(@Body uidRequest: UidRequest): Response<Unit>

    @GET("users/username/{firebaseId}")
    suspend fun getUsername(
        @Path("firebaseId") firebaseId: String
    ): Response<String>

    //todo unused logout route
    @POST("users/logout")
    suspend fun logout(): Response<Unit>

    //-----DRAWINGS ENDPOINTS --- //
    //gets all drawings for the user with that firebase id
    @GET("drawings/{firebaseId}")
    suspend fun getUserDrawings(
        @Path("firebaseId") firebaseId: String,
    ): Response<List<KtorAPIConnection.DrawingData>>


    @GET("drawings/load/{drawing_id}")
    suspend fun getDrawingById(
        @Path("drawing_id") id: Int
    ): Response<KtorAPIConnection.DrawingData>

    //creating a new drawing save in db
    @POST("drawings/create")
    suspend fun saveDrawing(
        @Body drawing: DrawingData // Use DrawingData for the request body
    ): Response<Unit>

    @PUT("drawings/update/{drawing_id}")
    suspend fun updateDrawing(
        @Path("drawing_id") id: Int,
        @Body drawing: DrawingData
    ): Response<Unit>

    @DELETE("drawings/delete/{drawing_id}")
    suspend fun deleteDrawing(@Path("drawing_id") id:Int): Response<Unit>

    //------SHARING----//
    @PUT("drawings/{drawing_id}/shareStatus")
    suspend fun updateDrawingShareStatus(
        @Path("drawing_id") drawingId: Int,
        @Body isShared: Boolean
    ): Response<Unit>

    @GET("drawings/shared")
    suspend fun fetchSharedDrawings(): Response<List<KtorAPIConnection.DrawingData>>

    //nested data class for drawing data
    data class DrawingData(
        val drawingId: Int,
        val name: String,
        val thumbnail: String?,
        val lastModified: String,
        val lines: String,
        val ownerId: String, // Firebase user ID
        val isShared: Boolean
    )
}
data class UidRequest(val id: String)

//data class UserRegistration(
//    val uid: String,
//    val username: String,
//    val email: String
//)



class APIServ(private val ktorAPIConnection: KtorAPIConnection) {


    suspend fun updateDrawingShareStatusServer(drawingId: Int, isShared: Boolean) {
        try {
            val response = ktorAPIConnection.updateDrawingShareStatus(drawingId, isShared)
            if (response.isSuccessful) {
                Log.d("ServerResponse", "Drawing share status updated successfully for ID: $drawingId")
            } else {
                Log.e("ServerResponse", "Error updating drawing share status: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Error updating drawing share status: ${e.message}")
        }
    }

    suspend fun fetchSharedDrawingsServer(): List<KtorAPIConnection.DrawingData>? {
        return try {
            val response = ktorAPIConnection.fetchSharedDrawings()
            if (response.isSuccessful) {
                Log.d("ServerResponse", "Shared drawings retrieved successfully")
                response.body()
            } else {
                Log.e("ServerResponse", "Error retrieving shared drawings: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Error retrieving shared drawings: ${e.message}")
            null
        }
    }

    suspend fun sendUidToServer(id: String) {
        try {
            val UidRequest = UidRequest(id)
            val response = ktorAPIConnection.sendUid(UidRequest)
            if (response.isSuccessful) {
                Log.d("ServerResponse", "Uid sent successfully")
            } else {
                Log.d("ServerResponse", "Error sending Uid: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Error sending Uid: ${e.message}")
        }
    }

    suspend fun sendDrawingToServer(drawing: KtorAPIConnection.DrawingData) {
        try {
            val response = ktorAPIConnection.saveDrawing(drawing)
            if (response.isSuccessful) {
                Log.d("ServerResponse", "Drawing saved successfully")
            } else {
                Log.d("ServerResponse", "Error saving drawing: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Error saving drawing: ${e.message}")
        }
    }

    suspend fun loadDrawingFromServer(drawingId: Int): KtorAPIConnection.DrawingData? {
        return try {
            val response = ktorAPIConnection.getDrawingById(drawingId) // Call to Retrofit API
            if (response.isSuccessful) {
                Log.d("ServerResponse", "Drawing loaded successfully for ID: $drawingId")
                response.body() // Return the DrawingData object
            } else {
                Log.e("ServerResponse", "Error loading drawing: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Error loading drawing: ${e.message}")
            null
        }
    }

    suspend fun getUserDrawingsFromServer(uid: String): List<KtorAPIConnection.DrawingData>? {
        return if (uid != null) {
            try {
                //Call the API method and retrieve the drawings
                val response = ktorAPIConnection.getUserDrawings(uid)
                if (response.isSuccessful) {
                    Log.d("ServerResponse", "Drawings retrieved successfully for user ID: $uid")
                    response.body() //list of DrawingData objects
                } else {
                    Log.d("ServerResponse", "Error retrieving drawings: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Error retrieving drawings: ${e.message}")
                null
            }
        } else {
            Log.e("NetworkError", "Firebase UID is null")
            null
        }
    }

    suspend fun deleteDrawingFromServer(drawingId: Int) {
        try {
            println("Attempting to delete drawing with ID: $drawingId")
            val response = ktorAPIConnection.deleteDrawing(drawingId)
            if (response.isSuccessful) {
                Log.d("ServerResponse", "Drawing deleted successfully with ID: $drawingId")
            } else {
                Log.e("ServerResponse", "Error deleting drawing: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Error deleting drawing: ${e.message}")
        }
    }


    suspend fun updateDrawingServer(drawingId: Int, updatedDrawing: KtorAPIConnection.DrawingData) {
        val response = ApiService.ktorAPIConnection.updateDrawing(drawingId, updatedDrawing)
        if (response.isSuccessful) {
            Log.d("UpdateDrawing", "Drawing updated successfully")
        } else {
            Log.e("UpdateDrawing", "Error updating drawing: ${response.errorBody()?.string()}")
        }
    }

}
