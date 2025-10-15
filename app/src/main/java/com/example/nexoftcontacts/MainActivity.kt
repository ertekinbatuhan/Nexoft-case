package com.example.nexoftcontacts

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexoftcontacts.data.repository.PhotoRepositoryImpl
import com.example.nexoftcontacts.presentation.screens.AddContactScreen
import com.example.nexoftcontacts.presentation.screens.ContactSuccessScreen
import com.example.nexoftcontacts.presentation.screens.ContactsScreen
import com.example.nexoftcontacts.presentation.screens.ContactDetailsScreen
import com.example.nexoftcontacts.presentation.components.ErrorSnackbar
import com.example.nexoftcontacts.presentation.components.SuccessSnackbar
import com.example.nexoftcontacts.presentation.event.ContactEvent
import com.example.nexoftcontacts.presentation.viewmodel.ContactViewModel
import com.example.nexoftcontacts.ui.theme.NexoftContactsTheme
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.utils.ContactsHelper
import android.Manifest
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexoftcontacts.ui.theme.BackgroundLight
import com.example.nexoftcontacts.ui.theme.Primary
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    val photoRepository: PhotoRepositoryImpl = viewModel.photoRepository
    val coroutineScope = rememberCoroutineScope()
    
    var showAddContactSheet by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var openInEditMode by remember { mutableStateOf(false) }
    var isSavedToPhone by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionDialogMessage by remember { mutableStateOf("") }
    var onSaveSuccess: (() -> Unit)? by remember { mutableStateOf(null) }
    
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
            selectedContact?.let { contact ->
                coroutineScope.launch(Dispatchers.Main) {
                    val success = withContext(Dispatchers.IO) {
                        ContactsHelper.saveContactToPhone(
                            context,
                            contact.firstName,
                            contact.lastName,
                            contact.phoneNumber
                        )
                    }
                    if (success) {
                        contact.id?.let { contactId ->
                            ContactsHelper.markContactAsSaved(context, contactId)
                            isSavedToPhone = isSavedToPhone + (contactId to true)
                            viewModel.onEvent(ContactEvent.RefreshContacts)
                            onSaveSuccess?.invoke()
                            onSaveSuccess = null
                        }
                    }
                }
            }
        } else {
            permissionDialogMessage = "Contacts permission is required to save contact to your phone. Please enable it in your device settings."
            showPermissionDialog = true
            onSaveSuccess = null
        }
    }
    
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(ContactEvent.OpenCamera(context))
        } else {
            permissionDialogMessage = "Camera permission is required to take photos. Please enable it in your device settings."
            showPermissionDialog = true
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
            openInEditMode = false
        },
        onContactEditClick = { contact ->
            selectedContact = contact
            openInEditMode = true
        }
    )
    
    if (showAddContactSheet) {
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
                showAddContactSheet = false
            },
            isLoading = operationState.isLoading,
            onCameraClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
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
                    
                    coroutineScope.launch(Dispatchers.Main) {
                        val hasPermission = withContext(Dispatchers.IO) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_CONTACTS
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        }
                        
                        if (hasPermission) {
                            val success = withContext(Dispatchers.IO) {
                                ContactsHelper.saveContactToPhone(
                                    context,
                                    contact.firstName,
                                    contact.lastName,
                                    contact.phoneNumber
                                )
                            }
                            if (success) {
                                contact.id?.let { contactId ->
                                    ContactsHelper.markContactAsSaved(context, contactId)
                                    isSavedToPhone = isSavedToPhone + (contactId to true)
                                    viewModel.onEvent(ContactEvent.RefreshContacts)
                                    onSuccess()
                                    onSaveSuccess = null
                                }
                            }
                        } else {
                            contactsPermissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
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
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onClearSelectedPhoto = {
                viewModel.onEvent(ContactEvent.ClearSelectedPhoto)
            }
        )
    }
    
    // Permission denied dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = {
                Text(
                    text = "Permission Required",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = permissionDialogMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("OK", color = Primary)
                }
            },
            containerColor = BackgroundLight
        )
    }
}