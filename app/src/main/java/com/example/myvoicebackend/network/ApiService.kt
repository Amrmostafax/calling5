package com.example.myvoicebackend.network

import com.example.myvoicebackend.models.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.GET

interface ApiService {
    @Multipart
    @POST("/api/chat")
    suspend fun uploadAudio(
        @Part audio: MultipartBody.Part
    ): ApiResponse

    @GET("/api/health")
    suspend fun healthCheck(): Map<String, Any>
}
