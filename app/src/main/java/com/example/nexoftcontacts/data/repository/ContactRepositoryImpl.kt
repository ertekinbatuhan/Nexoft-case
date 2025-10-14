package com.example.nexoftcontacts.data.repository

import android.content.Context
import com.example.nexoftcontacts.data.local.ContactDatabase
import com.example.nexoftcontacts.data.mapper.toDomain
import com.example.nexoftcontacts.data.mapper.toEntity
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.data.remote.ApiException
import com.example.nexoftcontacts.data.remote.ContactApiService
import com.example.nexoftcontacts.data.remote.NetworkModule
import com.example.nexoftcontacts.data.remote.mapper.toDomainModel
import com.example.nexoftcontacts.data.remote.mapper.toCreateUserRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.example.nexoftcontacts.domain.repository.ContactRepository

class ContactRepositoryImpl(
    private val context: Context,
    private val apiService: ContactApiService = NetworkModule.contactApiService
) : ContactRepository {
    
    private val database by lazy { ContactDatabase.getDatabase(context) }
    private val contactDao by lazy { database.contactDao() }
    
    private var cacheTimestamp: Long = 0
    private val CACHE_TIMEOUT = 5 * 60 * 1000L
    
    override suspend fun getAllContacts(forceRefresh: Boolean): Result<List<Contact>> {
        return try {
            val shouldFetchFromApi = forceRefresh || 
                                     System.currentTimeMillis() - cacheTimestamp > CACHE_TIMEOUT
            
            if (shouldFetchFromApi) {
                val response = apiService.getAllContacts(NetworkModule.API_KEY)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        val contacts = apiResponse.data.users.map { it.toDomainModel() }
                        
                        contactDao.clearCache()
                        contactDao.insertContacts(contacts.map { it.toEntity() })
                        cacheTimestamp = System.currentTimeMillis()
                        
                        Result.success(contacts)
                    } else {
                        val errorMessage = apiResponse?.messages?.joinToString(", ")
                        val cachedContacts = contactDao.getAllContacts().map { it.toDomain() }
                        if (cachedContacts.isNotEmpty()) {
                            Result.success(cachedContacts)
                        } else {
                            Result.failure(ApiException.Unknown(errorMessage ?: "Failed to fetch contacts"))
                        }
                    }
                } else {
                    val cachedContacts = contactDao.getAllContacts().map { it.toDomain() }
                    if (cachedContacts.isNotEmpty()) {
                        Result.success(cachedContacts)
                    } else {
                        Result.failure(ApiException.fromHttpCode(response.code(), "Failed to fetch contacts"))
                    }
                }
            } else {
                val cachedContacts = contactDao.getAllContacts().map { it.toDomain() }
                Result.success(cachedContacts)
            }
        } catch (e: Exception) {
            try {
                val cachedContacts = contactDao.getAllContacts().map { it.toDomain() }
                Result.success(cachedContacts)
            } catch (cacheError: Exception) {
                Result.failure(ApiException.NetworkError(e.message ?: "Network error occurred"))
            }
        }
    }
    
    override suspend fun getContactById(id: String): Result<Contact> {
        return try {
            val response = apiService.getContactById(NetworkModule.API_KEY, id)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    val contact = apiResponse.data.toDomainModel()
                    Result.success(contact)
                } else {
                    val errorMessage = apiResponse?.messages?.joinToString(", ")
                    Result.failure(ApiException.NotFound(errorMessage ?: "Contact not found"))
                }
            } else {
                Result.failure(ApiException.fromHttpCode(response.code(), "Failed to fetch contact"))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(e.message ?: "Network error occurred"))
        }
    }
    
    
    override suspend fun uploadImage(imageFile: File): Result<String> {
        return try {
            val fileExtension = imageFile.extension.lowercase()
            if (fileExtension != "png" && fileExtension != "jpg" && fileExtension != "jpeg") {
                return Result.failure(ApiException.InvalidFileFormat())
            }
            
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            
            val response = apiService.uploadImage(NetworkModule.API_KEY, imagePart)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    Result.success(apiResponse.data.imageUrl)
                } else {
                    val errorMessage = apiResponse?.messages?.joinToString(", ")
                    Result.failure(ApiException.Unknown(errorMessage ?: "Failed to upload image"))
                }
            } else {
                Result.failure(ApiException.fromHttpCode(response.code(), "Failed to upload image"))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(e.message ?: "Upload error occurred"))
        }
    }
    
    override suspend fun createUser(contact: Contact): Result<Contact> {
        return try {
            val request = contact.toCreateUserRequest()
            val response = apiService.createUser(NetworkModule.API_KEY, request)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    val createdContact = apiResponse.data.toDomainModel()
                    
                    contactDao.insertContact(createdContact.toEntity())
                    
                    Result.success(createdContact)
                } else {
                    val errorMessage = apiResponse?.messages?.joinToString(", ")
                    Result.failure(ApiException.Unknown(errorMessage ?: "Failed to create contact"))
                }
            } else {
                Result.failure(ApiException.fromHttpCode(response.code(), "Failed to create contact"))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(e.message ?: "Network error occurred"))
        }
    }
    
    override suspend fun updateUser(contact: Contact): Result<Contact> {
        return try {
            val contactId = contact.id ?: return Result.failure(ApiException.BadRequest("Contact ID is required"))
            val request = contact.toCreateUserRequest()
            val response = apiService.updateUser(NetworkModule.API_KEY, contactId, request)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    val updatedContact = apiResponse.data.toDomainModel()
                    
                    contactDao.updateContact(updatedContact.toEntity())
                    
                    Result.success(updatedContact)
                } else {
                    val errorMessage = apiResponse?.messages?.joinToString(", ")
                    Result.failure(ApiException.Unknown(errorMessage ?: "Failed to update contact"))
                }
            } else {
                Result.failure(ApiException.fromHttpCode(response.code(), "Failed to update contact"))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(e.message ?: "Network error occurred"))
        }
    }
    
    override suspend fun deleteUser(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteUser(NetworkModule.API_KEY, id)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    
                    contactDao.deleteContactById(id)
                    
                    Result.success(Unit)
                } else {
                    val errorMessage = apiResponse?.messages?.joinToString(", ")
                    Result.failure(ApiException.Unknown(errorMessage ?: "Failed to delete contact"))
                }
            } else {
                Result.failure(ApiException.fromHttpCode(response.code(), "Failed to delete contact"))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(e.message ?: "Network error occurred"))
        }
    }
}
