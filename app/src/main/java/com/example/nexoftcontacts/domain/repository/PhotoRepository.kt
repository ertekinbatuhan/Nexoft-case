package com.example.nexoftcontacts.domain.repository

import android.net.Uri

interface PhotoRepository {
    suspend fun capturePhotoFromCamera(): Result<Uri?>
    suspend fun selectPhotoFromGallery(): Result<Uri?>
}
