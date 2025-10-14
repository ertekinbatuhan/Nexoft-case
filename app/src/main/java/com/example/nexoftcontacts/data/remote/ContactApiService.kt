package com.example.nexoftcontacts.data.remote

import com.example.nexoftcontacts.data.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ContactApiService {
    
    @GET("api/User/GetAll")
    suspend fun getAllContacts(
        @Header("ApiKey") apiKey: String
    ): Response<ApiResponse<UsersData>>
    
    @GET("api/User/{id}")
    suspend fun getContactById(
        @Header("ApiKey") apiKey: String,
        @Path("id") contactId: String
    ): Response<ApiResponse<UserData>>
    
    @Multipart
    @POST("api/User/UploadImage")
    suspend fun uploadImage(
        @Header("ApiKey") apiKey: String,
        @Part image: MultipartBody.Part
    ): Response<ApiResponse<ImageUploadData>>
    
    @POST("api/User")
    suspend fun createUser(
        @Header("ApiKey") apiKey: String,
        @Body request: CreateUserRequest
    ): Response<ApiResponse<UserData>>
    
    @PUT("api/User/{id}")
    suspend fun updateUser(
        @Header("ApiKey") apiKey: String,
        @Path("id") userId: String,
        @Body request: CreateUserRequest
    ): Response<ApiResponse<UserData>>
    
    @DELETE("api/User/{id}")
    suspend fun deleteUser(
        @Header("ApiKey") apiKey: String,
        @Path("id") userId: String
    ): Response<ApiResponse<Unit>>
}
