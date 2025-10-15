package com.example.nexoftcontacts.presentation.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.nexoftcontacts.presentation.components.*
import com.example.nexoftcontacts.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    selectedPhotoUri: Uri? = null,
    initialFirstName: String? = null,
    initialLastName: String? = null,
    initialPhoneNumber: String? = null,
    onDismiss: () -> Unit,
    onSave: (firstName: String, lastName: String, phoneNumber: String) -> Unit,
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf(initialFirstName ?: "") }
    var lastName by remember { mutableStateOf(initialLastName ?: "") }
    var phoneNumber by remember { mutableStateOf(initialPhoneNumber ?: "") }
    var showPhotoPickerSheet by remember { mutableStateOf(false) }

    val isDoneEnabled =
        firstName.isNotBlank() && lastName.isNotBlank() && phoneNumber.isNotBlank() && !isLoading
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
                .fillMaxHeight(Dimens.modalHeightFraction)
                .padding(horizontal = Dimens.spaceLarge)
                .padding(bottom = Dimens.spaceXLarge)
        ) {
            // Header
            ContactFormHeader(
                title = "Edit Contact",
                isDoneEnabled = isDoneEnabled,
                isLoading = isLoading,
                onCancel = onDismiss,
                onDone = {
                    if (isDoneEnabled) {
                        onSave(firstName, lastName, phoneNumber)
                    }
                }
            )

            Spacer(modifier = Modifier.height(Dimens.spaceLarge))

            // Photo Section
            PhotoSection(
                selectedPhotoUri = selectedPhotoUri,
                onPhotoClick = { showPhotoPickerSheet = true },
                isEditMode = true
            )

            Spacer(modifier = Modifier.height(Dimens.spaceXLarge))

            // Form Fields
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
