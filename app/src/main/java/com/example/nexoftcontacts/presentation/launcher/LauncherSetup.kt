package com.example.nexoftcontacts.presentation.launcher

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.example.nexoftcontacts.data.repository.PhotoRepositoryImpl

/**
 * Centralized launcher setup for camera and gallery
 */
@Composable
fun rememberPhotoLaunchers(
    photoRepository: PhotoRepositoryImpl,
    onPhotoSelected: (Uri?) -> Unit
): PhotoLauncherState {
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val uri = photoRepository.getCurrentPhotoUri()
            onPhotoSelected(uri)
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onPhotoSelected(uri)
    }
    
    LaunchedEffect(Unit) {
        photoRepository.setCameraLauncher(cameraLauncher)
        photoRepository.setGalleryLauncher(galleryLauncher)
    }
    
    return PhotoLauncherState(
        cameraLauncher = cameraLauncher,
        galleryLauncher = galleryLauncher
    )
}

data class PhotoLauncherState(
    val cameraLauncher: androidx.activity.compose.ManagedActivityResultLauncher<Uri, Boolean>,
    val galleryLauncher: androidx.activity.compose.ManagedActivityResultLauncher<String, Uri?>
)
