package com.example.nexoftcontacts.presentation.screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.presentation.components.*
import com.example.nexoftcontacts.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    contact: Contact,
    selectedPhotoUri: Uri?,
    isSavedToPhone: Boolean = false,
    onDismiss: () -> Unit,
    onSaveToPhone: () -> Unit,
    onUpdateContact: (String, String, String, String) -> Unit,
    onDeleteContact: (String) -> Unit,
    onChangePhoto: () -> Unit,
    onClearSelectedPhoto: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var showChangePhotoSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editedFirstName by remember { mutableStateOf(contact.firstName ?: "") }
    var editedLastName by remember { mutableStateOf(contact.lastName ?: "") }
    var editedPhoneNumber by remember { mutableStateOf(contact.phoneNumber ?: "") }
    var dominantColor by remember { mutableStateOf(Primary) }
    
    val context = LocalContext.current
    
    // Extract dominant color from photo
    LaunchedEffect(contact.photoUri, selectedPhotoUri) {
        val photoUri = selectedPhotoUri ?: contact.photoUri?.let { Uri.parse(it) }
        photoUri?.let { uri ->
            try {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(uri)
                    .allowHardware(false)
                    .build()
                
                val result = (loader.execute(request) as? SuccessResult)?.drawable
                val bitmap = (result as? BitmapDrawable)?.bitmap
                
                bitmap?.let {
                    Palette.from(it).generate { palette ->
                        palette?.dominantSwatch?.rgb?.let { color ->
                            dominantColor = Color(color)
                        }
                    }
                }
            } catch (e: Exception) {
                // Keep default color
            }
        }
    }
    
    LaunchedEffect(contact.id) {
        onClearSelectedPhoto()
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(Dimens.modalHeightFraction)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = Dimens.spaceMedium)
                    .padding(bottom = Dimens.spaceXLarge)
            ) {
                // Top section - Edit mode or View mode
                ContactDetailsHeader(
                    isEditMode = isEditMode,
                    onCancelEdit = {
                        isEditMode = false
                        // Reset edited values
                        editedFirstName = contact.firstName ?: ""
                        editedLastName = contact.lastName ?: ""
                        editedPhoneNumber = contact.phoneNumber ?: ""
                    },
                    onDoneEdit = {
                        contact.id?.let { contactId ->
                            onUpdateContact(
                                contactId,
                                editedFirstName,
                                editedLastName,
                                editedPhoneNumber
                            )
                        }
                        isEditMode = false
                    },
                    onMenuClick = { showMenu = true },
                    showMenu = showMenu,
                    onDismissMenu = { showMenu = false },
                    onEditClick = {
                        showMenu = false
                        isEditMode = true
                    },
                    onDeleteClick = {
                        showMenu = false
                        showDeleteDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(Dimens.spacerHeight19))

                // Profile Photo Section
                ContactProfileSection(
                    photoUri = selectedPhotoUri ?: contact.photoUri?.let { Uri.parse(it) },
                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    dominantColor = dominantColor,
                    onChangePhotoClick = {
                        if (isEditMode) {
                            onChangePhoto()
                        } else {
                            showChangePhotoSheet = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(Dimens.spaceLarge))

                // Form Fields
                ContactDetailsFormFields(
                    firstName = if (isEditMode) editedFirstName else (contact.firstName ?: ""),
                    lastName = if (isEditMode) editedLastName else (contact.lastName ?: ""),
                    phoneNumber = if (isEditMode) editedPhoneNumber else (contact.phoneNumber ?: ""),
                    isEditMode = isEditMode,
                    onFirstNameChange = { editedFirstName = it },
                    onLastNameChange = { editedLastName = it },
                    onPhoneNumberChange = { editedPhoneNumber = it }
                )

                if (!isEditMode) {
                    Spacer(modifier = Modifier.height(Dimens.spaceXLarge))

                    // Save to Phone Section
                    SaveToPhoneSection(
                        isSavedToPhone = isSavedToPhone,
                        onSaveClick = {
                            if (!isSavedToPhone) {
                                showSuccessMessage = true
                                onSaveToPhone()
                            }
                        }
                    )
                }

                if (showSuccessMessage) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        SuccessSnackbar(
                            showSnackbar = showSuccessMessage,
                            onDismiss = { showSuccessMessage = false },
                            message = "User is added to your phone!"
                        )
                    }
                }
            }
        }
    }
    
    SuccessSnackbar(
        showSnackbar = showSuccessMessage,
        onDismiss = { showSuccessMessage = false },
        message = "User is added to your phone!"
    )
    
    // Change Photo Sheet
    if (showChangePhotoSheet) {
        ChangePhotoSheet(
            contact = contact,
            selectedPhotoUri = selectedPhotoUri,
            onDismiss = {
                showChangePhotoSheet = false
            },
            onUpdateContact = { contactId, firstName, lastName, phoneNumber ->
                onUpdateContact(contactId, firstName, lastName, phoneNumber)
                showChangePhotoSheet = false
            },
            onChangePhoto = onChangePhoto
        )
    }
    
    // Delete Contact Dialog
    if (showDeleteDialog) {
        DeleteContactDialog(
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                contact.id?.let { contactId ->
                    onDeleteContact(contactId)
                    onDismiss() // Close the contact details screen after deletion
                }
            }
        )
    }
}


