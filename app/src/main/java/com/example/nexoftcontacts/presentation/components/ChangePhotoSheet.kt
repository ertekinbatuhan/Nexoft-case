package com.example.nexoftcontacts.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePhotoSheet(
    contact: Contact,
    selectedPhotoUri: Uri?,
    onDismiss: () -> Unit,
    onUpdateContact: (String, String, String, String) -> Unit,
    onChangePhoto: () -> Unit
) {
    var editedFirstName by remember { mutableStateOf(contact.firstName ?: "") }
    var editedLastName by remember { mutableStateOf(contact.lastName ?: "") }
    var editedPhoneNumber by remember { mutableStateOf(contact.phoneNumber ?: "") }
    
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null
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
                // Header - Without title, just Cancel and Done buttons
                ContactFormHeader(
                    title = "",
                    isDoneEnabled = true,
                    onCancel = onDismiss,
                    onDone = {
                        contact.id?.let { contactId ->
                            onUpdateContact(
                                contactId,
                                editedFirstName,
                                editedLastName,
                                editedPhoneNumber
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(Dimens.spacerHeight19))
                
                // Profile Photo Section
                ContactProfileSection(
                    photoUri = selectedPhotoUri ?: contact.photoUri?.let { Uri.parse(it) },
                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    onChangePhotoClick = onChangePhoto
                )
                
                Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                
                // Form Fields
                ContactDetailsFormFields(
                    firstName = editedFirstName,
                    lastName = editedLastName,
                    phoneNumber = editedPhoneNumber,
                    isEditMode = true,
                    onFirstNameChange = { editedFirstName = it },
                    onLastNameChange = { editedLastName = it },
                    onPhoneNumberChange = { editedPhoneNumber = it }
                )
            }
        }
    }
}
