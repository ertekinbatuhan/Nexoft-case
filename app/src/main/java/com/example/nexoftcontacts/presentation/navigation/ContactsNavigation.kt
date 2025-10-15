package com.example.nexoftcontacts.presentation.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.presentation.event.ContactEvent
import com.example.nexoftcontacts.presentation.launcher.PhotoLauncherState
import com.example.nexoftcontacts.presentation.permissions.PermissionHandlerState
import com.example.nexoftcontacts.presentation.screens.AddContactScreen
import com.example.nexoftcontacts.presentation.screens.ContactDetailsScreen
import com.example.nexoftcontacts.presentation.screens.ContactSuccessScreen
import com.example.nexoftcontacts.presentation.screens.ContactsScreen
import com.example.nexoftcontacts.presentation.components.ErrorSnackbar
import com.example.nexoftcontacts.presentation.viewmodel.ContactViewModel
import com.example.nexoftcontacts.utils.ContactsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main contacts navigation and screen management
 */
@Composable
fun ContactsNavigation(
    viewModel: ContactViewModel,
    context: Context,
    coroutineScope: CoroutineScope,
    permissionHandler: PermissionHandlerState,
    photoLaunchers: PhotoLauncherState
) {
    var showAddContactSheet by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var openInEditMode by remember { mutableStateOf(false) }
    var isSavedToPhone by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var onSaveSuccess: (() -> Unit)? by remember { mutableStateOf(null) }
    
    // Check if contact is saved to phone
    LaunchedEffect(selectedContact?.id) {
        selectedContact?.id?.let { contactId ->
            val isSaved = ContactsHelper.isContactSavedToPhone(context, contactId)
            isSavedToPhone = isSavedToPhone + (contactId to isSaved)
        }
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operationState by viewModel.operationState.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    
    // Main contacts list screen
    ContactsScreen(
        contacts = uiState.filteredContacts,
        searchQuery = uiState.searchQuery,
        searchHistory = searchHistory,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        showDeleteSuccess = operationState.isDeleteSuccess,
        showUpdateSuccess = operationState.isUpdateSuccess,
        onAddContactClick = {
            showAddContactSheet = true
        },
        onRefresh = {
            viewModel.onEvent(ContactEvent.RefreshContacts)
        },
        onErrorDismiss = {
            viewModel.onEvent(ContactEvent.ClearErrorMessage)
        },
        onSearchQueryChange = { query ->
            viewModel.onEvent(ContactEvent.UpdateSearchQuery(query))
        },
        onClearSearch = {
            viewModel.onEvent(ContactEvent.ClearSearch)
            viewModel.onEvent(ContactEvent.LoadSearchHistory)
        },
        onSearchFocusChanged = { focused ->
            if (focused) {
                viewModel.onEvent(ContactEvent.LoadSearchHistory)
            }
        },
        onHistoryItemClick = { query ->
            viewModel.onEvent(ContactEvent.SelectSearchHistory(query))
        },
        onRemoveHistoryItem = { query ->
            viewModel.onEvent(ContactEvent.RemoveSearchHistory(query))
        },
        onClearSearchHistory = {
            viewModel.onEvent(ContactEvent.ClearSearchHistory)
        },
        onDeleteContact = { contactId ->
            viewModel.onEvent(ContactEvent.DeleteContact(contactId, context))
        },
        onDeleteSuccessDismiss = {
            viewModel.onEvent(ContactEvent.ClearDeleteSuccess)
        },
        onUpdateSuccessDismiss = {
            viewModel.onEvent(ContactEvent.ClearUpdateSuccess)
        },
        onContactClick = { contact ->
            selectedContact = contact
            openInEditMode = false
        },
        onContactEditClick = { contact ->
            selectedContact = contact
            openInEditMode = true
        }
    )
    
    // Add contact sheet
    if (showAddContactSheet && !operationState.isSuccess) {
        AddContactScreen(
            selectedPhotoUri = viewModel.selectedPhotoUri.value,
            onDismiss = {
                showAddContactSheet = false
                viewModel.onEvent(ContactEvent.ClearSelectedPhoto)
            },
            onSave = { firstName, lastName, phoneNumber ->
                viewModel.onEvent(
                    ContactEvent.AddContact(
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phoneNumber,
                        context = context
                    )
                )
                // Don't close sheet yet, wait for success screen
            },
            isLoading = operationState.isLoading,
            onCameraClick = {
                permissionHandler.requestCameraPermission {
                    viewModel.onEvent(ContactEvent.OpenCamera(context))
                }
            },
            onGalleryClick = {
                viewModel.onEvent(ContactEvent.OpenGallery(context))
            }
        )
    }
    
    // Success screen
    if (operationState.isSuccess) {
        ContactSuccessScreen(
            onDismiss = {
                showAddContactSheet = false
                viewModel.onEvent(ContactEvent.ClearOperationState)
                viewModel.onEvent(ContactEvent.ClearSelectedPhoto)
            }
        )
    }
    
    // Error snackbars
    ErrorSnackbar(
        showSnackbar = operationState.errorMessage != null,
        onDismiss = {
            viewModel.onEvent(ContactEvent.ClearOperationState)
        },
        message = operationState.errorMessage ?: "An error occurred"
    )
    
    ErrorSnackbar(
        showSnackbar = uiState.errorMessage != null,
        onDismiss = {
            viewModel.onEvent(ContactEvent.ClearErrorMessage)
        },
        message = uiState.errorMessage ?: "An error occurred"
    )
    
    // Contact details screen
    selectedContact?.let { contact ->
        ContactDetailsScreen(
            contact = contact,
            selectedPhotoUri = viewModel.selectedPhotoUri.value,
            isSavedToPhone = isSavedToPhone[contact.id] ?: false,
            initialEditMode = openInEditMode,
            onDismiss = {
                selectedContact = null
                openInEditMode = false
                viewModel.onEvent(ContactEvent.ClearSelectedPhoto)
            },
            onSaveToPhone = { onSuccess ->
                if (isSavedToPhone[contact.id] != true) {
                    onSaveSuccess = onSuccess
                    
                    permissionHandler.requestContactsPermission {
                        coroutineScope.launch(Dispatchers.IO) {
                            val success = ContactsHelper.saveContactToPhone(
                                context,
                                contact.firstName,
                                contact.lastName,
                                contact.phoneNumber
                            )
                            if (success) {
                                contact.id?.let { contactId ->
                                    ContactsHelper.markContactAsSaved(context, contactId)
                                    withContext(Dispatchers.Main) {
                                        isSavedToPhone = isSavedToPhone + (contactId to true)
                                        viewModel.onEvent(ContactEvent.RefreshContacts)
                                        onSuccess()
                                        onSaveSuccess = null
                                    }
                                }
                            }
                        }
                    }
                }
            },
            onUpdateContact = { contactId, firstName, lastName, phoneNumber ->
                viewModel.onEvent(
                    ContactEvent.UpdateContact(
                        contactId = contactId,
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phoneNumber,
                        context = context
                    )
                )
                selectedContact = null
            },
            onDeleteContact = { contactId ->
                viewModel.onEvent(ContactEvent.DeleteContact(contactId, context))
                selectedContact = null
            },
            onChangePhoto = {
                viewModel.onEvent(ContactEvent.OpenGallery(context))
            },
            onCameraClick = {
                permissionHandler.requestCameraPermission {
                    viewModel.onEvent(ContactEvent.OpenCamera(context))
                }
            },
            onClearSelectedPhoto = {
                viewModel.onEvent(ContactEvent.ClearSelectedPhoto)
            }
        )
    }
}
