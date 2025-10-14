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
import com.example.nexoftcontacts.data.repository.PhotoRepositoryImpl
import com.example.nexoftcontacts.domain.usecase.PhotoPickerUseCase
import com.example.nexoftcontacts.presentation.components.AddContactBottomSheet
import com.example.nexoftcontacts.presentation.screens.ContactListScreen
import com.example.nexoftcontacts.presentation.viewmodel.ContactViewModel
import com.example.nexoftcontacts.ui.theme.NexoftContactsTheme

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
    
    ContactListScreen(
        contacts = viewModel.contacts,
        onAddContactClick = {
            showAddContactSheet = true
        }
    )
    
    if (showAddContactSheet) {
        AddContactBottomSheet(
            selectedPhotoUri = viewModel.selectedPhotoUri,
            onDismiss = {
                showAddContactSheet = false
                viewModel.clearSelectedPhoto()
            },
            onSave = { firstName, lastName, phoneNumber ->
                viewModel.addContact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )
                showAddContactSheet = false
            },
            onCameraClick = {
                viewModel.capturePhotoFromCamera()
            },
            onGalleryClick = {
                viewModel.selectPhotoFromGallery()
            }
        )
    }
}