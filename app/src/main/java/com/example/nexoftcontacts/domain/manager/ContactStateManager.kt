package com.example.nexoftcontacts.domain.manager

import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.presentation.state.ContactOperationState
import com.example.nexoftcontacts.presentation.state.ContactUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactStateManager @Inject constructor() {
    
    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()
    
    private val _operationState = MutableStateFlow(ContactOperationState())
    val operationState: StateFlow<ContactOperationState> = _operationState.asStateFlow()
    
    fun setUiLoading() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
    }
    
    fun setUiRefreshing() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
    }
    
    fun setUiError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isRefreshing = false,
            errorMessage = message
        )
    }
    
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun updateContacts(
        contacts: List<Contact>,
        filteredContacts: List<Contact>
    ) {
        _uiState.value = _uiState.value.copy(
            contacts = contacts,
            filteredContacts = filteredContacts,
            isLoading = false,
            isRefreshing = false,
            errorMessage = null
        )
    }
    
    fun updateSearchQuery(query: String, filteredContacts: List<Contact>) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredContacts = filteredContacts
        )
    }
    
    fun clearSearch(allContacts: List<Contact>) {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            filteredContacts = allContacts
        )
    }
    
    fun setSearchFocus(isFocused: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchFocused = isFocused)
    }
    
    fun setOperationLoading() {
        _operationState.value = _operationState.value.copy(
            isLoading = true,
            errorMessage = null
        )
    }
    
    fun setOperationSuccess() {
        _operationState.value = _operationState.value.copy(
            isLoading = false,
            isSuccess = true,
            errorMessage = null
        )
    }
    
    fun setOperationDeleteSuccess() {
        _operationState.value = _operationState.value.copy(
            isLoading = false,
            isDeleteSuccess = true,
            errorMessage = null
        )
    }
    
    fun setOperationUpdateSuccess() {
        _operationState.value = _operationState.value.copy(
            isLoading = false,
            isUpdateSuccess = true,
            errorMessage = null
        )
    }
    
    fun setOperationError(message: String) {
        _operationState.value = _operationState.value.copy(
            isLoading = false,
            isSuccess = false,
            errorMessage = message
        )
    }
    
    fun clearOperationState() {
        _operationState.value = ContactOperationState()
    }
    
    fun clearDeleteSuccess() {
        _operationState.value = _operationState.value.copy(isDeleteSuccess = false)
    }
    
    fun clearUpdateSuccess() {
        _operationState.value = _operationState.value.copy(isUpdateSuccess = false)
    }
    
    fun getCurrentContacts(): List<Contact> = _uiState.value.contacts
    
    fun getCurrentSearchQuery(): String = _uiState.value.searchQuery
}
