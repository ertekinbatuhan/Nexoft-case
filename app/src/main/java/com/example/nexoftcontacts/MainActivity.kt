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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexoftcontacts.data.repository.PhotoRepositoryImpl
import com.example.nexoftcontacts.domain.usecase.PhotoPickerUseCase
import com.example.nexoftcontacts.presentation.screens.AddContactScreen
import com.example.nexoftcontacts.presentation.screens.ContactSuccessScreen
import com.example.nexoftcontacts.presentation.screens.ContactsScreen
import com.example.nexoftcontacts.presentation.screens.ContactDetailsScreen
import com.example.nexoftcontacts.presentation.viewmodel.ContactViewModel
import com.example.nexoftcontacts.ui.theme.NexoftContactsTheme
import com.example.nexoftcontacts.data.model.Contact

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
fun ContactsApp() {
    val context = LocalContext.current
    val photoRepository = remember { PhotoRepositoryImpl(context) }
    val photoPickerUseCase = remember { PhotoPickerUseCase(photoRepository) }
    val viewModel: ContactViewModel = viewModel { ContactViewModel(photoPickerUseCase) }
    
    var showAddContactSheet by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val uri = photoRepository.getCurrentPhotoUri()
            viewModel.setSelectedPhoto(uri)
        }
    }
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setSelectedPhoto(uri)
    }
    
    // Set launchers to repository
    LaunchedEffect(Unit) {
        photoRepository.setCameraLauncher(cameraLauncher)
        photoRepository.setGalleryLauncher(galleryLauncher)
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operationState by viewModel.operationState.collectAsStateWithLifecycle()
    
    ContactsScreen(
        contacts = uiState.filteredContacts,
        searchQuery = uiState.searchQuery,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        showDeleteSuccess = operationState.isDeleteSuccess,
        onAddContactClick = {
            showAddContactSheet = true
        },
        onRefresh = {
            viewModel.refreshContacts()
        },
        onErrorDismiss = {
            viewModel.clearErrorMessage()
        },
        onSearchQueryChange = { query ->
            viewModel.updateSearchQuery(query)
        },
        onClearSearch = {
            viewModel.clearSearch()
        },
        onDeleteContact = { contactId ->
            viewModel.deleteContact(contactId)
        },
        onDeleteSuccessDismiss = {
            viewModel.clearDeleteSuccess()
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
                viewModel.clearSelectedPhoto()
            },
            onSave = { firstName, lastName, phoneNumber ->
                viewModel.addContact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    context = context
                )
                showAddContactSheet = false
            },
            isLoading = operationState.isLoading,
            onCameraClick = {
                viewModel.capturePhotoFromCamera()
            },
            onGalleryClick = {
                viewModel.selectPhotoFromGallery()
            }
        )
    }
    
    // Success Screen - show when operation is successful
    if (operationState.isSuccess) {
        ContactSuccessScreen(
            onDismiss = {
                viewModel.clearOperationState()
            }
        )
    }
    
    // Contact Details Screen
    selectedContact?.let { contact ->
        ContactDetailsScreen(
            contact = contact,
            selectedPhotoUri = viewModel.selectedPhotoUri,
            onDismiss = {
                selectedContact = null
                viewModel.clearSelectedPhoto()
            },
            onSaveToPhone = {

            },
            onUpdateContact = { contactId, firstName, lastName, phoneNumber ->
                viewModel.updateContact(
                    contactId = contactId,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    context = context
                )
                selectedContact = null
            },
            onDeleteContact = { contactId ->
                viewModel.deleteContact(contactId)
                selectedContact = null
            },
            onChangePhoto = {
                viewModel.selectPhotoFromGallery()
            },
            onClearSelectedPhoto = {
                viewModel.clearSelectedPhoto()
            }
        )
    }
}