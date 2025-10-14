package com.example.nexoftcontacts.presentation.state

import com.example.nexoftcontacts.data.model.Contact

data class ContactUiState(
    val contacts: List<Contact> = emptyList(),
    val filteredContacts: List<Contact> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

data class ContactOperationState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val errorMessage: String? = null
)
