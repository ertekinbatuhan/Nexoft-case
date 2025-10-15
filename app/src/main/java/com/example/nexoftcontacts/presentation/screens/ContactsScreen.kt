package com.example.nexoftcontacts.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nexoftcontacts.presentation.components.*
import com.example.nexoftcontacts.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    contacts: List<com.example.nexoftcontacts.data.model.Contact>,
    searchQuery: String = "",
    searchHistory: List<String> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    showDeleteSuccess: Boolean = false,
    showUpdateSuccess: Boolean = false,
    onAddContactClick: () -> Unit,
    onRefresh: () -> Unit = {},
    onErrorDismiss: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onSearchFocusChanged: (Boolean) -> Unit = {},
    onHistoryItemClick: (String) -> Unit = {},
    onRemoveHistoryItem: (String) -> Unit = {},
    onClearSearchHistory: () -> Unit = {},
    onDeleteContact: (String) -> Unit = {},
    onDeleteSuccessDismiss: () -> Unit = {},
    onUpdateSuccessDismiss: () -> Unit = {},
    onContactClick: (com.example.nexoftcontacts.data.model.Contact) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSearchFocused by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contacts",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = onAddContactClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Primary,
                            contentColor = BackgroundLight
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact",
                            modifier = Modifier.size(Dimens.iconLarge)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundGray
                )
            )
        },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onClearSearch = onClearSearch,
                    onFocusChanged = { focused ->
                        isSearchFocused = focused
                        onSearchFocusChanged(focused)
                    },
                    placeholder = "Search by name",
                    modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
                )
                
                // Show search history when search is focused and empty
                if (isSearchFocused && searchQuery.isEmpty() && searchHistory.isNotEmpty()) {
                    SearchHistory(
                        searchHistory = searchHistory,
                        onHistoryItemClick = { query ->
                            onHistoryItemClick(query)
                        },
                        onRemoveHistoryItem = onRemoveHistoryItem,
                        onClearAll = onClearSearchHistory,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (isLoading) {
                    // Show loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else if (contacts.isEmpty()) {
                    if (searchQuery.isNotBlank()) {
                        NoSearchResults(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        NoContactsEmptyState(
                            onCreateNewContactClick = onAddContactClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    ContactList(
                        contacts = contacts,
                        searchQuery = searchQuery,
                        onDeleteContact = onDeleteContact,
                        onContactClick = onContactClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            SuccessSnackbar(
                showSnackbar = showDeleteSuccess,
                onDismiss = onDeleteSuccessDismiss,
                message = "User is deleted!"
            )
            
            SuccessSnackbar(
                showSnackbar = showUpdateSuccess,
                onDismiss = onUpdateSuccessDismiss,
                message = "User is updated!"
            )
        }
    }
}

