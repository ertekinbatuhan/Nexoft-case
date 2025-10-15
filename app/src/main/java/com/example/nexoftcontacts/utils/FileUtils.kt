package com.example.nexoftcontacts.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    
    private const val MAX_IMAGE_SIZE = 1024 // Maximum width/height in pixels
    private const val COMPRESSION_QUALITY = 80 // JPEG quality (0-100)
    
    // Allowed image formats
    private val ALLOWED_IMAGE_TYPES = listOf("image/jpeg", "image/jpg", "image/png")
    
    /**
     * Validates if the URI points to an allowed image format (PNG or JPG)
     */
    fun isValidImageFormat(context: Context, uri: Uri): Boolean {
        return try {
            val mimeType = context.contentResolver.getType(uri)
            
            // Check MIME type directly
            if (mimeType != null && ALLOWED_IMAGE_TYPES.contains(mimeType.lowercase())) {
                return true
            }
            
            // Fallback: Check file extension
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            val extensionMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            
            extensionMimeType != null && ALLOWED_IMAGE_TYPES.contains(extensionMimeType.lowercase())
        } catch (e: Exception) {
            false
        }
    }
    
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            // Validate image format first
            if (!isValidImageFormat(context, uri)) {
                throw IllegalArgumentException("Only PNG and JPG formats are allowed")
            }
            
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            
            inputStream?.use { input ->
                // Decode and compress the image
                val bitmap = BitmapFactory.decodeStream(input)
                val compressedBitmap = compressImage(bitmap)
                
                // Get orientation from EXIF data
                val orientation = getOrientation(context, uri)
                val rotatedBitmap = rotateBitmap(compressedBitmap, orientation)
                
                // Save compressed image
                FileOutputStream(tempFile).use { output ->
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, output)
                }
                
                rotatedBitmap.recycle()
                if (rotatedBitmap != compressedBitmap) {
                    compressedBitmap.recycle()
                }
            }
            
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun compressImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        // If image is already small enough, return as is
        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return bitmap
        }
        
        // Calculate scaling factor
        val scale = if (width > height) {
            MAX_IMAGE_SIZE.toFloat() / width
        } else {
            MAX_IMAGE_SIZE.toFloat() / height
        }
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    private fun getOrientation(context: Context, uri: Uri): Int {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val exif = ExifInterface(it)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }
    
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

