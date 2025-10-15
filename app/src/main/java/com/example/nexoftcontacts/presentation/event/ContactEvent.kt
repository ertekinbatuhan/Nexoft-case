package com.example.nexoftcontacts.presentation.event

import android.content.Context
import android.net.Uri
import com.example.nexoftcontacts.data.model.Contact

sealed class ContactEvent {
    // Contact CRUD Operations
    data class AddContact(
        val firstName: String,
        val lastName: String,
        val phoneNumber: String,
        val context: Context? = null
    ) : ContactEvent()
    
    data class UpdateContact(
        val contactId: String,
        val firstName: String,
        val lastName: String,
        val phoneNumber: String,
        val context: Context? = null
    ) : ContactEvent()
    
    data class UpdateContactObject(val contact: Contact) : ContactEvent()
    
    data class DeleteContact(
        val contactId: String,
        val context: Context? = null
    ) : ContactEvent()
    
    // Load & Refresh Operations
    object LoadContacts : ContactEvent()
    object RefreshContacts : ContactEvent()
    
    // Photo Operations
    data class SetSelectedPhoto(val uri: Uri?) : ContactEvent()
    object ClearSelectedPhoto : ContactEvent()
    data class OpenCamera(val context: Context) : ContactEvent()
    data class OpenGallery(val context: Context) : ContactEvent()
    
    // Search Operations
    data class UpdateSearchQuery(val query: String) : ContactEvent()
    object ClearSearch : ContactEvent()
    data class SelectSearchHistory(val query: String) : ContactEvent()
    data class RemoveSearchHistory(val query: String) : ContactEvent()
    object ClearSearchHistory : ContactEvent()
    
    // State Management Operations
    object ClearOperationState : ContactEvent()
    object ClearErrorMessage : ContactEvent()
    object ClearDeleteSuccess : ContactEvent()
    object ClearUpdateSuccess : ContactEvent()
    object LoadSearchHistory : ContactEvent()
}
