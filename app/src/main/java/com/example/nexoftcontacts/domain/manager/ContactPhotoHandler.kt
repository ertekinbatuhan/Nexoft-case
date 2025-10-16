package com.example.nexoftcontacts.domain.manager

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.nexoftcontacts.domain.repository.ContactRepository
import com.example.nexoftcontacts.utils.FileUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactPhotoHandler @Inject constructor(
    private val repository: ContactRepository
) {
    
    private val _selectedPhotoUri = mutableStateOf<Uri?>(null)
    val selectedPhotoUri: State<Uri?> = _selectedPhotoUri
    
    fun setSelectedPhoto(uri: Uri?) {
        _selectedPhotoUri.value = uri
    }
    
    fun clearSelectedPhoto() {
        _selectedPhotoUri.value = null
    }
    
    suspend fun uploadPhotoIfSelected(context: Context?): Result<String?> {
        val uri = _selectedPhotoUri.value
        
        if (uri == null || context == null) {
            return Result.success(null)
        }
        
        if (!FileUtils.isValidImageFormat(context, uri)) {
            return Result.failure(Exception("Only PNG and JPG image formats are allowed"))
        }
        
        val imageFile = FileUtils.uriToFile(context, uri)
            ?: return Result.success(null)
        
        return repository.uploadImage(imageFile)
    }
    
    fun getCurrentPhotoUri(): Uri? = _selectedPhotoUri.value
}
