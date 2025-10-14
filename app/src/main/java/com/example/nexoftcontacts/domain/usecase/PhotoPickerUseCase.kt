package com.example.nexoftcontacts.domain.usecase

import android.net.Uri
import com.example.nexoftcontacts.domain.repository.PhotoRepository

class PhotoPickerUseCase(
    private val photoRepository: PhotoRepository
) {
    suspend fun captureFromCamera(): Result<Uri?> {
        return photoRepository.capturePhotoFromCamera()
    }
    
    suspend fun selectFromGallery(): Result<Uri?> {
        return photoRepository.selectPhotoFromGallery()
    }
}
