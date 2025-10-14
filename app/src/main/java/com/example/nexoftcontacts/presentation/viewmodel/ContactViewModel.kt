package com.example.nexoftcontacts.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.domain.usecase.PhotoPickerUseCase
import kotlinx.coroutines.launch
import java.util.UUID

class ContactViewModel(
    private val photoPickerUseCase: PhotoPickerUseCase
) : ViewModel() {
    
    val contacts = mutableStateListOf<Contact>()
    
    
    private val _selectedPhotoUri = mutableStateOf<Uri?>(null)
    val selectedPhotoUri: Uri? get() = _selectedPhotoUri.value
    
    // Photo picker functions
    fun capturePhotoFromCamera() {
        viewModelScope.launch {
            photoPickerUseCase.captureFromCamera()
                .onSuccess { uri ->
                    _selectedPhotoUri.value = uri
                }
                .onFailure { 
                    // Handle error
                }
        }
    }
    
    fun selectPhotoFromGallery() {
        viewModelScope.launch {
            photoPickerUseCase.selectFromGallery()
                .onSuccess { uri ->
                    _selectedPhotoUri.value = uri
                }
                .onFailure { 
                    // Handle error
                }
        }
    }
    
    fun setSelectedPhoto(uri: Uri?) {
        _selectedPhotoUri.value = uri
    }
    
    fun clearSelectedPhoto() {
        _selectedPhotoUri.value = null
    }
    
    // Yeni kişi ekle
    fun addContact(firstName: String, lastName: String, phoneNumber: String, photoUri: String? = null) {
        val newContact = Contact(
            id = UUID.randomUUID().toString(),
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            photoUri = photoUri ?: _selectedPhotoUri.value?.toString()
        )
        contacts.add(newContact)
        println("DEBUG: Contact added - ${newContact.firstName} ${newContact.lastName}")
        println("DEBUG: Total contacts: ${contacts.size}")
        println("DEBUG: All contacts: ${contacts.map { "${it.firstName} ${it.lastName}" }}")
        clearSelectedPhoto() // Clear photo after adding contact
    }
    
    // Kişi sil
    fun deleteContact(contactId: String) {
        contacts.removeIf { it.id == contactId }
    }
    
    // Kişi güncelle
    fun updateContact(contact: Contact) {
        val index = contacts.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            contacts[index] = contact
        }
    }
}

