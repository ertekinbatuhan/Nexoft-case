package com.example.nexoftcontacts.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.nexoftcontacts.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactBottomSheet(
    selectedPhotoUri: Uri? = null,
    onDismiss: () -> Unit,
    onSave: (firstName: String, lastName: String, phoneNumber: String) -> Unit,
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showPhotoPickerSheet by remember { mutableStateOf(false) }
    
    val isDoneEnabled = firstName.isNotBlank() && lastName.isNotBlank() && phoneNumber.isNotBlank()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = BackgroundLight,
        modifier = modifier,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = Dimens.spaceLarge)
                .padding(bottom = Dimens.spaceXLarge)
        ) {
            ContactFormHeader(
                title = "New Contact",
                isDoneEnabled = isDoneEnabled,
                onCancel = {
                    // Clear form when canceling
                    firstName = ""
                    lastName = ""
                    phoneNumber = ""
                    onDismiss()
                },
                onDone = {
                    if (isDoneEnabled) {
                        onSave(firstName, lastName, phoneNumber)
                        // Clear form after saving
                        firstName = ""
                        lastName = ""
                        phoneNumber = ""
                        onDismiss()
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
            
            PhotoSection(
                selectedPhotoUri = selectedPhotoUri,
                onPhotoClick = { showPhotoPickerSheet = true },
                isEditMode = false
            )
            
            Spacer(modifier = Modifier.height(Dimens.spaceXLarge))
            
            ContactFormFields(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                onFirstNameChange = { firstName = it },
                onLastNameChange = { lastName = it },
                onPhoneNumberChange = { phoneNumber = it }
            )
        }
    }
    
    // Photo Picker Bottom Sheet
    if (showPhotoPickerSheet) {
        PhotoPickerBottomSheet(
            onDismiss = { showPhotoPickerSheet = false },
            onCameraClick = { 
                onCameraClick()
                showPhotoPickerSheet = false
            },
            onGalleryClick = { 
                onGalleryClick()
                showPhotoPickerSheet = false
            }
        )
    }
}
