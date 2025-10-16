package com.example.nexoftcontacts.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.data.repository.PhotoRepositoryImpl
import com.example.nexoftcontacts.domain.repository.ContactRepository
import com.example.nexoftcontacts.utils.ContactsHelper
import com.example.nexoftcontacts.domain.usecase.CreateContactUseCase
import com.example.nexoftcontacts.domain.usecase.UpdateContactUseCase
import com.example.nexoftcontacts.domain.usecase.DeleteContactUseCase
import com.example.nexoftcontacts.domain.usecase.GetContactsUseCase
import com.example.nexoftcontacts.domain.manager.ContactStateManager
import com.example.nexoftcontacts.domain.manager.ContactPhotoHandler
import com.example.nexoftcontacts.domain.manager.ContactSearchHandler
import com.example.nexoftcontacts.presentation.event.ContactEvent
import com.example.nexoftcontacts.presentation.state.ContactOperationState
import com.example.nexoftcontacts.presentation.state.ContactUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ContactRepository,
    private val getContactsUseCase: GetContactsUseCase,
    private val createContactUseCase: CreateContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val stateManager: ContactStateManager,
    private val photoHandler: ContactPhotoHandler,
    private val searchHandler: ContactSearchHandler,
    val photoRepository: PhotoRepositoryImpl
) : ViewModel() {
    
    val uiState: StateFlow<ContactUiState> = stateManager.uiState
    val operationState: StateFlow<ContactOperationState> = stateManager.operationState
    val selectedPhotoUri: State<Uri?> = photoHandler.selectedPhotoUri
    val searchHistory: StateFlow<List<String>> = searchHandler.searchHistory
    
    init {
        loadCachedContacts()
        loadContacts()
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
            is ContactEvent.SetSelectedPhoto -> photoHandler.setSelectedPhoto(event.uri)
            ContactEvent.ClearSelectedPhoto -> photoHandler.clearSelectedPhoto()
            is ContactEvent.OpenCamera -> openCamera(event.context)
            is ContactEvent.OpenGallery -> openGallery(event.context)
            is ContactEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            ContactEvent.ClearSearch -> clearSearch()
            is ContactEvent.SelectSearchHistory -> selectHistoryItem(event.query)
            is ContactEvent.RemoveSearchHistory -> removeFromHistory(event.query)
            ContactEvent.ClearSearchHistory -> clearSearchHistory()
            is ContactEvent.SetSearchFocus -> setSearchFocus(event.isFocused)
            ContactEvent.ClearOperationState -> stateManager.clearOperationState()
            ContactEvent.ClearErrorMessage -> stateManager.clearErrorMessage()
            ContactEvent.ClearDeleteSuccess -> stateManager.clearDeleteSuccess()
            ContactEvent.ClearUpdateSuccess -> stateManager.clearUpdateSuccess()
            ContactEvent.LoadSearchHistory -> searchHandler.loadSearchHistory()
        }
    }
    private suspend fun updateContactsWithDeviceStatus(contacts: List<Contact>) {
        val contactsWithDeviceStatus = contacts.map { contact ->
            val isDeviceContact = contact.id?.let { 
                ContactsHelper.isContactSavedToPhone(context, it)
            } ?: false
            contact.copy(isDeviceContact = isDeviceContact)
        }
        
        val currentQuery = stateManager.getCurrentSearchQuery()
        val filteredContacts = searchHandler.filterContacts(contactsWithDeviceStatus, currentQuery)
        
        stateManager.updateContacts(contactsWithDeviceStatus, filteredContacts)
    }
    
    private fun loadCachedContacts() {
        viewModelScope.launch {
            try {
                val cachedContacts = repository.getCachedContacts()
                if (cachedContacts.isNotEmpty()) {
                    updateContactsWithDeviceStatus(cachedContacts)
                }
            } catch (e: Exception) {
                // Ignore cache errors, will load from API
            }
        }
    }
    
    private fun loadContacts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            stateManager.setUiLoading()
            
            getContactsUseCase(forceRefresh)
                .onSuccess { contacts ->
                    updateContactsWithDeviceStatus(contacts)
                }
                .onFailure { exception ->
                    stateManager.setUiError(exception.message ?: "Unknown error occurred")
                }
        }
    }
    
    private fun refreshContacts() {
        viewModelScope.launch {
            stateManager.setUiRefreshing()
            
            getContactsUseCase(forceRefresh = true)
                .onSuccess { contacts ->
                    updateContactsWithDeviceStatus(contacts)
                }
                .onFailure { exception ->
                    stateManager.setUiError(exception.message ?: "Unknown error occurred")
                }
        }
    }
    
    private fun addContact(firstName: String, lastName: String, phoneNumber: String, context: Context? = null) {
        viewModelScope.launch {
            stateManager.setOperationLoading()
            
            try {
                val uploadResult = photoHandler.uploadPhotoIfSelected(context)
                
                if (uploadResult.isFailure) {
                    stateManager.setOperationError(uploadResult.exceptionOrNull()?.message ?: "Image upload failed")
                    return@launch
                }
                
                val imageUrl = uploadResult.getOrNull()
                
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
                        stateManager.setOperationSuccess()
                        loadContacts()
                        photoHandler.clearSelectedPhoto()
                    }
                    .onFailure { exception ->
                        stateManager.setOperationError(exception.message ?: "Failed to create contact")
                    }
            } catch (e: Exception) {
                stateManager.setOperationError(e.message ?: "An error occurred")
            }
        }
    }
    

    
    private fun openCamera(context: Context) {
        viewModelScope.launch {
            photoRepository.capturePhotoFromCamera()
        }
    }
    
    private fun openGallery(context: Context) {
        viewModelScope.launch {
            photoRepository.selectPhotoFromGallery()
                .onSuccess { uri ->
                    uri?.let { photoHandler.setSelectedPhoto(it) }
                }
        }
    }
    
    private fun updateContact(contact: Contact) {
        viewModelScope.launch {
            stateManager.setOperationLoading()
            
            try {
                updateContactUseCase(contact)
                    .onSuccess { updatedContact ->
                        stateManager.setOperationSuccess()
                        loadContacts()
                    }
                    .onFailure { exception ->
                        stateManager.setOperationError(exception.message ?: "Failed to update contact")
                    }
            } catch (e: Exception) {
                stateManager.setOperationError(e.message ?: "An error occurred")
            }
        }
    }
    
    private fun updateContact(contactId: String, firstName: String, lastName: String, phoneNumber: String, context: Context? = null) {
        viewModelScope.launch {
            stateManager.setOperationLoading()
            
            try {
                val uploadResult = photoHandler.uploadPhotoIfSelected(context)
                
                if (uploadResult.isFailure) {
                    stateManager.setOperationError(uploadResult.exceptionOrNull()?.message ?: "Image upload failed")
                    return@launch
                }
                
                var imageUrl = uploadResult.getOrNull()
                
                if (imageUrl == null) {
                    val currentContact = stateManager.getCurrentContacts().find { it.id == contactId }
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
                        stateManager.setOperationUpdateSuccess()
                        photoHandler.clearSelectedPhoto()
                        loadContacts()
                    }
                    .onFailure { exception ->
                        stateManager.setOperationError(exception.message ?: "Failed to update contact")
                    }
            } catch (e: Exception) {
                stateManager.setOperationError(e.message ?: "An error occurred")
            }
        }
    }
    
    private fun deleteContact(contactId: String, context: Context? = null) {
        viewModelScope.launch {
            stateManager.setOperationLoading()
            
            try {
                deleteContactUseCase(contactId)
                    .onSuccess {
                        context?.let {
                            ContactsHelper.removeSavedContact(it, contactId)
                        }
                        
                        stateManager.setOperationDeleteSuccess()
                        loadContacts()
                    }
                    .onFailure { exception ->
                        stateManager.setOperationError(exception.message ?: "Failed to delete contact")
                    }
            } catch (e: Exception) {
                stateManager.setOperationError(e.message ?: "An error occurred")
            }
        }
    }
    
    private fun updateSearchQuery(query: String) {
        val contacts = stateManager.getCurrentContacts()
        val filteredContacts = searchHandler.filterContacts(contacts, query)
        stateManager.updateSearchQuery(query, filteredContacts)
        
        searchHandler.cancelDebounceAndPrepareForSave()
        
        if (query.isNotBlank()) {
            val debounceJob = viewModelScope.launch {
                kotlinx.coroutines.delay(1000L)
                searchHandler.saveToSearchHistory(query)
            }
            searchHandler.setDebounceJob(debounceJob)
        }
    }
    
    private fun clearSearch() {
        val contacts = stateManager.getCurrentContacts()
        stateManager.clearSearch(contacts)
    }
    
    private fun removeFromHistory(query: String) {
        searchHandler.removeFromHistory(query)
    }
    
    private fun clearSearchHistory() {
        searchHandler.clearSearchHistory()
    }
    
    private fun selectHistoryItem(query: String) {
        updateSearchQuery(query)
    }
    
    private fun setSearchFocus(isFocused: Boolean) {
        stateManager.setSearchFocus(isFocused)
        if (isFocused) {
            searchHandler.loadSearchHistory()
        }
    }
}