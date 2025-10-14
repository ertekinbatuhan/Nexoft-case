package com.example.nexoftcontacts.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("messages")
    val messages: List<String>,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("status")
    val status: Int
)

data class UsersData(
    @SerializedName("users")
    val users: List<UserDto>
)

data class UserData(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?
)

data class UserDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?
)

data class ImageUploadData(
    @SerializedName("imageUrl")
    val imageUrl: String
)

data class CreateUserRequest(
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?
)
