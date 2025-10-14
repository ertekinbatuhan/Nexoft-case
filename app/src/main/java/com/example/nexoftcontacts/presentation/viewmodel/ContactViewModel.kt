package com.example.nexoftcontacts.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.data.repository.ContactRepositoryImpl
import com.example.nexoftcontacts.utils.FileUtils
import com.example.nexoftcontacts.domain.usecase.CreateContactUseCase
import com.example.nexoftcontacts.domain.usecase.UpdateContactUseCase
import com.example.nexoftcontacts.domain.usecase.DeleteContactUseCase
import com.example.nexoftcontacts.domain.usecase.GetContactsUseCase
import com.example.nexoftcontacts.domain.usecase.PhotoPickerUseCase
import com.example.nexoftcontacts.presentation.state.ContactOperationState
import com.example.nexoftcontacts.presentation.state.ContactUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ContactViewModel(
    private val photoPickerUseCase: PhotoPickerUseCase,
    private val getContactsUseCase: GetContactsUseCase = GetContactsUseCase(ContactRepositoryImpl()),
    private val createContactUseCase: CreateContactUseCase = CreateContactUseCase(ContactRepositoryImpl()),
    private val updateContactUseCase: UpdateContactUseCase = UpdateContactUseCase(ContactRepositoryImpl()),
    private val deleteContactUseCase: DeleteContactUseCase = DeleteContactUseCase(ContactRepositoryImpl())
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()
    
    // Operation State (for create, update, delete)
    private val _operationState = MutableStateFlow(ContactOperationState())
    val operationState: StateFlow<ContactOperationState> = _operationState.asStateFlow()
    
    // Photo picker state
    private val _selectedPhotoUri = mutableStateOf<Uri?>(null)
    val selectedPhotoUri: Uri? get() = _selectedPhotoUri.value
    
    init {
        loadContacts()
    }
    
    // Load contacts from API
    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            getContactsUseCase()
                .onSuccess { contacts ->
                    _uiState.value = _uiState.value.copy(
                        contacts = contacts,
                        filteredContacts = filterContacts(contacts, _uiState.value.searchQuery),
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }
    
    // Refresh contacts
    fun refreshContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            getContactsUseCase()
                .onSuccess { contacts ->
                    _uiState.value = _uiState.value.copy(
                        contacts = contacts,
                        filteredContacts = filterContacts(contacts, _uiState.value.searchQuery),
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        errorMessage = exception.message ?: "Failed to refresh contacts"
                    )
                }
        }
    }
    
    // Create new contact using new API
    fun addContact(firstName: String, lastName: String, phoneNumber: String, context: Context? = null) {
        viewModelScope.launch {
            _operationState.value = _operationState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                var imageUrl: String? = null
                
                _selectedPhotoUri.value?.let { uri ->
                    if (context != null) {
                        val imageFile = FileUtils.uriToFile(context, uri)
                        
                        if (imageFile != null) {
                            val repository = ContactRepositoryImpl()
                            repository.uploadImage(imageFile)
                                .onSuccess { uploadedUrl ->
                                    imageUrl = uploadedUrl
                                }
                                .onFailure { exception ->
                                    imageUrl = null
                                }
                        } else {
                            imageUrl = null
                        }
                    } else {
                        imageUrl = null
                    }
                }
                
                val newContact = Contact(
                    id = null,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    photoUri = imageUrl,
                    isDeviceContact = false
                )
                
                val repository = ContactRepositoryImpl()
                
                repository.createUser(newContact)
                    .onSuccess { createdContact ->
                        _operationState.value = _operationState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                        loadContacts()
                        clearSelectedPhoto()
                    }
                    .onFailure { exception ->
                        _operationState.value = _operationState.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = exception.message ?: "Failed to create contact"
                        )
                    }
            } catch (e: Exception) {
                _operationState.value = _operationState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
    }
    
    // Clear operation state
    fun clearOperationState() {
        _operationState.value = ContactOperationState()
    }
    
    // Clear error message
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
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
    
    // Update contact
    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            _operationState.value = _operationState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                updateContactUseCase(contact)
                    .onSuccess { updatedContact ->
                        _operationState.value = _operationState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                        loadContacts()
                    }
                    .onFailure { exception ->
                        _operationState.value = _operationState.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = exception.message ?: "Failed to update contact"
                        )
                    }
            } catch (e: Exception) {
                _operationState.value = _operationState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
    }
    
    // Delete contact
    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            _operationState.value = _operationState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                deleteContactUseCase(contactId)
                    .onSuccess {
                        _operationState.value = _operationState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                        loadContacts()
                    }
                    .onFailure { exception ->
                        _operationState.value = _operationState.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = exception.message ?: "Failed to delete contact"
                        )
                    }
            } catch (e: Exception) {
                _operationState.value = _operationState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredContacts = filterContacts(_uiState.value.contacts, query)
        )
    }
    
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            filteredContacts = _uiState.value.contacts
        )
    }
    
    private fun filterContacts(contacts: List<Contact>, query: String): List<Contact> {
        if (query.isBlank()) {
            return contacts
        }
        
        val lowercaseQuery = query.lowercase().trim()
        return contacts.filter { contact ->
            val fullName = "${contact.firstName ?: ""} ${contact.lastName ?: ""}".lowercase()
            val firstName = contact.firstName?.lowercase() ?: ""
            val lastName = contact.lastName?.lowercase() ?: ""
            
            fullName.contains(lowercaseQuery) ||
            firstName.contains(lowercaseQuery) ||
            lastName.contains(lowercaseQuery)
        }
    }
}