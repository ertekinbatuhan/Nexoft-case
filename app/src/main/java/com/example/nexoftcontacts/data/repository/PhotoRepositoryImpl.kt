package com.example.nexoftcontacts.data.repository

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.nexoftcontacts.domain.repository.PhotoRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PhotoRepositoryImpl(
    private val context: Context
) : PhotoRepository {
    
    private var currentPhotoUri: Uri? = null
    private var cameraLauncher: ActivityResultLauncher<Uri>? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null
    
    fun setCameraLauncher(launcher: ActivityResultLauncher<Uri>) {
        cameraLauncher = launcher
    }
    
    fun setGalleryLauncher(launcher: ActivityResultLauncher<String>) {
        galleryLauncher = launcher
    }
    
    override suspend fun capturePhotoFromCamera(): Result<Uri?> = suspendCoroutine { continuation ->
        try {
            val photoFile = createImageFile()
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            currentPhotoUri = photoUri
            cameraLauncher?.launch(photoUri)
            continuation.resume(Result.success(photoUri))
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }
    
    override suspend fun selectPhotoFromGallery(): Result<Uri?> = suspendCoroutine { continuation ->
        try {
            galleryLauncher?.launch("image/*")
            continuation.resume(Result.success(null)) // URI will be returned via callback
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }
    
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = File(context.cacheDir, "images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }
    
    fun getCurrentPhotoUri(): Uri? = currentPhotoUri
}
