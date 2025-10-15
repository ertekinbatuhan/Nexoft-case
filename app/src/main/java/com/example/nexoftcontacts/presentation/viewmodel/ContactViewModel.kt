package com.example.nexoftcontacts.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.domain.repository.ContactRepository
import com.example.nexoftcontacts.data.repository.ContactRepositoryImpl
import com.example.nexoftcontacts.utils.FileUtils
import com.example.nexoftcontacts.utils.SearchHistoryManager
import com.example.nexoftcontacts.utils.ContactsHelper
import com.example.nexoftcontacts.domain.usecase.CreateContactUseCase
import com.example.nexoftcontacts.domain.usecase.UpdateContactUseCase
import com.example.nexoftcontacts.domain.usecase.DeleteContactUseCase
import com.example.nexoftcontacts.domain.usecase.GetContactsUseCase
import com.example.nexoftcontacts.domain.usecase.PhotoPickerUseCase
import com.example.nexoftcontacts.presentation.event.ContactEvent
import com.example.nexoftcontacts.presentation.state.ContactOperationState
import com.example.nexoftcontacts.presentation.state.ContactUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.UUID

class ContactViewModel(
    private val context: Context,
    private val photoPickerUseCase: PhotoPickerUseCase,
    repository: ContactRepository? = null
) : ViewModel() {
    
    private val repository: ContactRepository = repository ?: ContactRepositoryImpl(context)
    private val getContactsUseCase = GetContactsUseCase(this.repository)
    private val createContactUseCase = CreateContactUseCase(this.repository)
    private val updateContactUseCase = UpdateContactUseCase(this.repository)
    private val deleteContactUseCase = DeleteContactUseCase(this.repository)
    private val searchHistoryManager = SearchHistoryManager(context)
    
    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()
    
    private val _operationState = MutableStateFlow(ContactOperationState())
    val operationState: StateFlow<ContactOperationState> = _operationState.asStateFlow()
    
    private val _selectedPhotoUri = mutableStateOf<Uri?>(null)
    val selectedPhotoUri: Uri? get() = _selectedPhotoUri.value
    
    // Debounce job for search history
    private var searchDebounceJob: Job? = null
    private val SEARCH_DEBOUNCE_DELAY = 1000L // 1 second
    
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    
    init {
        loadContacts()
        loadSearchHistory()
    }
    
    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.AddContact -> addContact(
                event.firstName,
                event.lastName,
                event.phoneNumber,
                event.context
            )
            is ContactEvent.UpdateContact -> updateContact(
                event.contactId,
                event.firstName,
                event.lastName,
                event.phoneNumber,
                event.context
            )
            is ContactEvent.UpdateContactObject -> updateContact(event.contact)
            is ContactEvent.DeleteContact -> deleteContact(event.contactId, event.context)
            ContactEvent.LoadContacts -> loadContacts()
            ContactEvent.RefreshContacts -> refreshContacts()
            is ContactEvent.SetSelectedPhoto -> setSelectedPhoto(event.uri)
            ContactEvent.ClearSelectedPhoto -> clearSelectedPhoto()
            is ContactEvent.OpenCamera -> openCamera(event.context)
            is ContactEvent.OpenGallery -> openGallery(event.context)
            is ContactEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            ContactEvent.ClearSearch -> clearSearch()
            is ContactEvent.SelectSearchHistory -> selectHistoryItem(event.query)
            is ContactEvent.RemoveSearchHistory -> removeFromHistory(event.query)
            ContactEvent.ClearSearchHistory -> clearSearchHistory()
            ContactEvent.ClearOperationState -> clearOperationState()
            ContactEvent.ClearErrorMessage -> clearErrorMessage()
            ContactEvent.ClearDeleteSuccess -> clearDeleteSuccess()
            ContactEvent.ClearUpdateSuccess -> clearUpdateSuccess()
            ContactEvent.LoadSearchHistory -> loadSearchHistory()
        }
    }
    
    private fun setOperationLoading() {
        _operationState.value = _operationState.value.copy(
            isLoading = true,
            errorMessage = null
        )
    }
    
    private fun setOperationSuccess() {
        _operationState.value = _operationState.value.copy(
            isLoading = false,
            isSuccess = true,
            errorMessage = null
        )
    }
    
    private fun setOperationDeleteSuccess() {
        _operationState.value = _operationState.value.copy(
            isLoading = false,
            isDeleteSuccess = true,
            errorMessage = null
        )
    }
    
    private fun setOperationUpdateSuccess() {
        _operationState.value = _operationState.value.copy(
            isLoading = false,
            isUpdateSuccess = true,
            errorMessage = null
        )
    }
    
    private fun setOperationError(message: String) {
        _operationState.value = _operationState.value.copy(
            isLoading = false,
            isSuccess = false,
            errorMessage = message
        )
    }
    
    private fun setUiLoading() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
    }
    
    private fun setUiRefreshing() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
    }
    
    private fun setUiError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isRefreshing = false,
            errorMessage = message
        )
    }
    
    private suspend fun updateContactsWithDeviceStatus(contacts: List<Contact>) {
        val contactsWithDeviceStatus = contacts.map { contact ->
            val isDeviceContact = contact.id?.let { 
                ContactsHelper.isContactSavedToPhone(context, it)
            } ?: false
            contact.copy(isDeviceContact = isDeviceContact)
        }
        
        _uiState.value = _uiState.value.copy(
            contacts = contactsWithDeviceStatus,
            filteredContacts = filterContacts(contactsWithDeviceStatus, _uiState.value.searchQuery),
            isLoading = false,
            isRefreshing = false,
            errorMessage = null
        )
    }
    
    private fun loadContacts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            setUiLoading()
            
            getContactsUseCase(forceRefresh)
                .onSuccess { contacts ->
                    updateContactsWithDeviceStatus(contacts)
                }
                .onFailure { exception ->
                    setUiError(exception.message ?: "Unknown error occurred")
                }
        }
    }
    
    private fun refreshContacts() {
        viewModelScope.launch {
            setUiRefreshing()
            
            getContactsUseCase(forceRefresh = true)
                .onSuccess { contacts ->
                    updateContactsWithDeviceStatus(contacts)
                }
                .onFailure { exception ->
                    setUiError(exception.message ?: "Unknown error occurred")
                }
        }
    }
    
    private fun addContact(firstName: String, lastName: String, phoneNumber: String, context: Context? = null) {
        viewModelScope.launch {
            setOperationLoading()
            
            try {
                var imageUrl: String? = null
                
                _selectedPhotoUri.value?.let { uri ->
                    if (context != null) {
                        if (!FileUtils.isValidImageFormat(context, uri)) {
                            setOperationError("Only PNG and JPG image formats are allowed")
                            return@launch
                        }
                        
                        val imageFile = FileUtils.uriToFile(context, uri)
                        
                        if (imageFile != null) {
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
                
                repository.createUser(newContact)
                    .onSuccess { createdContact ->
                        setOperationSuccess()
                        loadContacts()
                        clearSelectedPhoto()
                    }
                    .onFailure { exception ->
                        setOperationError(exception.message ?: "Failed to create contact")
                    }
            } catch (e: Exception) {
                setOperationError(e.message ?: "An error occurred")
            }
        }
    }
    
    private fun clearOperationState() {
        _operationState.value = ContactOperationState()
    }
    
    private fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    private fun openCamera(context: Context) {
        viewModelScope.launch {
            photoPickerUseCase.captureFromCamera()
                .onSuccess { uri ->
                    _selectedPhotoUri.value = uri
                }
                .onFailure { 
                }
        }
    }
    
    private fun openGallery(context: Context) {
        viewModelScope.launch {
            photoPickerUseCase.selectFromGallery()
                .onSuccess { uri ->
                    _selectedPhotoUri.value = uri
                }
                .onFailure { 
                }
        }
    }
    
    private fun setSelectedPhoto(uri: Uri?) {
        _selectedPhotoUri.value = uri
    }
    
    private fun clearSelectedPhoto() {
        _selectedPhotoUri.value = null
    }
    
    private fun updateContact(contact: Contact) {
        viewModelScope.launch {
            setOperationLoading()
            
            try {
                updateContactUseCase(contact)
                    .onSuccess { updatedContact ->
                        setOperationSuccess()
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
    
    private fun updateContact(contactId: String, firstName: String, lastName: String, phoneNumber: String, context: Context? = null) {
        viewModelScope.launch {
            setOperationLoading()
            
            try {
                var imageUrl: String? = null
                
                _selectedPhotoUri.value?.let { uri ->
                    if (context != null) {
                        if (!FileUtils.isValidImageFormat(context, uri)) {
                            setOperationError("Only PNG and JPG image formats are allowed")
                            return@launch
                        }
                        
                        val imageFile = FileUtils.uriToFile(context, uri)
                        
                        if (imageFile != null) {
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
                
                if (imageUrl == null) {
                    val currentContact = _uiState.value.contacts.find { it.id == contactId }
                    imageUrl = currentContact?.photoUri
                }
                
                val contact = Contact(
                    id = contactId,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    photoUri = imageUrl,
                    isDeviceContact = false
                )
                
                updateContactUseCase(contact)
                    .onSuccess { updatedContact ->
                        setOperationUpdateSuccess()
                        clearSelectedPhoto()
                        loadContacts()
                    }
                    .onFailure { exception ->
                        setOperationError(exception.message ?: "Failed to update contact")
                    }
            } catch (e: Exception) {
                setOperationError(e.message ?: "An error occurred")
            }
        }
    }
    
    private fun deleteContact(contactId: String, context: Context? = null) {
        viewModelScope.launch {
            setOperationLoading()
            
            try {
                deleteContactUseCase(contactId)
                    .onSuccess {
                        context?.let {
                            com.example.nexoftcontacts.utils.ContactsHelper.removeSavedContact(it, contactId)
                        }
                        
                        setOperationDeleteSuccess()
                        loadContacts()
                    }
                    .onFailure { exception ->
                        setOperationError(exception.message ?: "Failed to delete contact")
                    }
            } catch (e: Exception) {
                setOperationError(e.message ?: "An error occurred")
                
            }
        }
    }
    
    private fun clearDeleteSuccess() {
        _operationState.value = _operationState.value.copy(isDeleteSuccess = false)
    }
    
    private fun clearUpdateSuccess() {
        _operationState.value = _operationState.value.copy(isUpdateSuccess = false)
    }
    
    private fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredContacts = filterContacts(_uiState.value.contacts, query)
        )
        
        // Cancel previous debounce job
        searchDebounceJob?.cancel()
        
        // Save to history after user stops typing (debounce)
        if (query.isNotBlank()) {
            searchDebounceJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                searchHistoryManager.addSearchQuery(query.trim())
                loadSearchHistory()
            }
        }
    }
    
    private fun submitSearch(query: String) {
        // Cancel debounce and save immediately
        searchDebounceJob?.cancel()
        
        if (query.isNotBlank()) {
            searchHistoryManager.addSearchQuery(query.trim())
            loadSearchHistory()
        }
    }
    
    private fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            filteredContacts = _uiState.value.contacts
        )
    }
    
    private fun loadSearchHistory() {
        _searchHistory.value = searchHistoryManager.getSearchHistory()
    }
    
    private fun removeFromHistory(query: String) {
        searchHistoryManager.removeSearchQuery(query)
        loadSearchHistory()
    }
    
    private fun clearSearchHistory() {
        searchHistoryManager.clearHistory()
        loadSearchHistory()
    }
    
    private fun selectHistoryItem(query: String) {
        updateSearchQuery(query)
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