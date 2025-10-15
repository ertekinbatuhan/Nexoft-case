package com.example.nexoftcontacts

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexoftcontacts.data.repository.PhotoRepositoryImpl
import com.example.nexoftcontacts.presentation.screens.AddContactScreen
import com.example.nexoftcontacts.presentation.screens.ContactSuccessScreen
import com.example.nexoftcontacts.presentation.screens.ContactsScreen
import com.example.nexoftcontacts.presentation.screens.ContactDetailsScreen
import com.example.nexoftcontacts.presentation.components.ErrorSnackbar
import com.example.nexoftcontacts.presentation.event.ContactEvent
import com.example.nexoftcontacts.presentation.viewmodel.ContactViewModel
import com.example.nexoftcontacts.ui.theme.NexoftContactsTheme
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.utils.ContactsHelper
import android.Manifest
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexoftContactsTheme {
                ContactsApp()
            }
        }
    }
}

@Composable
fun ContactsApp(
    viewModel: ContactViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // Explicitly cast to PhotoRepositoryImpl to access launcher setters
    val photoRepository: PhotoRepositoryImpl = viewModel.photoRepository
    val coroutineScope = rememberCoroutineScope()
    
    var showAddContactSheet by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var contactToSave by remember { mutableStateOf<Contact?>(null) }
    var isSavedToPhone by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    
    LaunchedEffect(selectedContact?.id) {
        selectedContact?.id?.let { contactId ->
            val isSaved = ContactsHelper.isContactSavedToPhone(context, contactId)
            isSavedToPhone = isSavedToPhone + (contactId to isSaved)
        }
    }
    
    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contactToSave?.let { contact ->
                coroutineScope.launch {
                    val success = ContactsHelper.saveContactToPhone(
                        context,
                        contact.firstName,
                        contact.lastName,
                        contact.phoneNumber
                    )
                    if (success) {
                        contact.id?.let { 
                            ContactsHelper.markContactAsSaved(context, it)
                            isSavedToPhone = isSavedToPhone + (it to true)
                            viewModel.onEvent(ContactEvent.RefreshContacts)
                        }
                    }
                }
            }
            contactToSave = null
        }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val uri = photoRepository.getCurrentPhotoUri()
            viewModel.onEvent(ContactEvent.SetSelectedPhoto(uri))
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onEvent(ContactEvent.SetSelectedPhoto(uri))
    }
    
    // Set launchers to the injected PhotoRepository instance
    LaunchedEffect(Unit) {
        photoRepository.setCameraLauncher(cameraLauncher)
        photoRepository.setGalleryLauncher(galleryLauncher)
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operationState by viewModel.operationState.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    
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
        }
    )
    
    if (showAddContactSheet) {
        AddContactScreen(
            selectedPhotoUri = viewModel.selectedPhotoUri,
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
                showAddContactSheet = false
            },
            isLoading = operationState.isLoading,
            onCameraClick = {
                viewModel.onEvent(ContactEvent.OpenCamera(context))
            },
            onGalleryClick = {
                viewModel.onEvent(ContactEvent.OpenGallery(context))
            }
        )
    }
    
    if (operationState.isSuccess) {
        ContactSuccessScreen(
            onDismiss = {
                viewModel.onEvent(ContactEvent.ClearOperationState)
            }
        )
    }
    
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
    
    selectedContact?.let { contact ->
        ContactDetailsScreen(
            contact = contact,
            selectedPhotoUri = viewModel.selectedPhotoUri,
            isSavedToPhone = isSavedToPhone[contact.id] ?: false,
            onDismiss = {
                selectedContact = null
                viewModel.onEvent(ContactEvent.ClearSelectedPhoto)
            },
            onSaveToPhone = {
                contactToSave = contact
                contactsPermissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
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
            onClearSelectedPhoto = {
                viewModel.onEvent(ContactEvent.ClearSelectedPhoto)
            }
        )
    }
}