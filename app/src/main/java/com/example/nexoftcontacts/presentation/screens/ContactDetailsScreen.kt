package com.example.nexoftcontacts.presentation.screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.nexoftcontacts.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.DpOffset
import coil.compose.SubcomposeAsyncImage
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.presentation.components.DeleteContactDialog
import com.example.nexoftcontacts.presentation.components.SuccessSnackbar
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
                if (isEditMode) {
                    // Edit mode header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.spaceMedium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                isEditMode = false
                                // Reset edited values
                                editedFirstName = contact.firstName ?: ""
                                editedLastName = contact.lastName ?: ""
                                editedPhoneNumber = contact.phoneNumber ?: ""
                            }
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.displaySmall
                            )
                        }

                        Text(
                            text = "Edit Contact",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        TextButton(
                            onClick = {
                                contact.id?.let { contactId ->
                                    onUpdateContact(
                                        contactId,
                                        editedFirstName,
                                        editedLastName,
                                        editedPhoneNumber
                                    )
                                }
                                isEditMode = false
                            }
                        ) {
                            Text(
                                text = "Done",
                                style = MaterialTheme.typography.displayMedium
                            )
                        }
                    }
                } else {
                    // View mode header with menu icon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.spaceXLarge)
                    ) {
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            IconButton(
                                onClick = { showMenu = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu",
                                    tint = IconBlack
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                offset = DpOffset(x = 0.dp, y = -Dimens.spaceSmall),
                                modifier = Modifier
                                    .background(White)
                            ) {
                                // Edit option
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Edit",
                                                style = DropdownTextStyles.menuItem.copy(
                                                    color = TextPrimary
                                                )
                                            )
                                            Icon(
                                                painter = painterResource(id = R.drawable.edit),
                                                contentDescription = null,
                                                tint = TextPrimary,
                                                modifier = Modifier
                                                    .size(Dimens.iconLarge)
                                                    .padding(Dimens.spaceXSmall)
                                            )
                                        }
                                    },
                                    onClick = {
                                        showMenu = false
                                        isEditMode = true
                                    }
                                )

                                // Delete option
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Delete",
                                                style = DropdownTextStyles.menuItem.copy(
                                                    color = Error
                                                )
                                            )
                                            Icon(
                                                painter = painterResource(id = R.drawable.delete),
                                                contentDescription = null,
                                                tint = Error,
                                                modifier = Modifier
                                                    .size(Dimens.iconLarge)
                                                    .padding(start = 3.dp, top = 3.dp, end = 3.dp, bottom = 3.dp)
                                            )
                                        }
                                    },
                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spacerHeight19))

                // Profile Photo
                Box(
                    modifier = Modifier
                        .size(Dimens.avatarLarge)
                        .align(Alignment.CenterHorizontally)
                ) {
                    val photoUri = selectedPhotoUri ?: contact.photoUri?.let { Uri.parse(it) }
                    
                    if (photoUri != null) {
                        SubcomposeAsyncImage(
                            model = photoUri,
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(Dimens.avatarLarge)
                                .shadow(
                                    elevation = Dimens.elevationLarge,
                                    shape = CircleShape,
                                    ambientColor = dominantColor.copy(alpha = 0.5f),
                                    spotColor = dominantColor.copy(alpha = 0.5f)
                                )
                                .clip(CircleShape),
                            loading = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(Dimens.avatarLarge)
                                )
                            },
                            error = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(Dimens.avatarLarge)
                                )
                            }
                        )
                    } else {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(Dimens.avatarLarge)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                // Change Photo Button
                TextButton(
                    onClick = { 
                        if (isEditMode) {
                            onChangePhoto()
                        } else {
                            showChangePhotoSheet = true
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Change Photo",
                        style = CustomTextStyles.changePhotoButton
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.spaceLarge))

                // First Name Field
                OutlinedTextField(
                    value = if (isEditMode) editedFirstName else (contact.firstName ?: ""),
                    onValueChange = { editedFirstName = it },
                    readOnly = !isEditMode,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderLight,
                        focusedBorderColor = Primary
                    ),
                    shape = RoundedCornerShape(Dimens.radiusSmall)
                )

                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                // Last Name Field
                OutlinedTextField(
                    value = if (isEditMode) editedLastName else (contact.lastName ?: ""),
                    onValueChange = { editedLastName = it },
                    readOnly = !isEditMode,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderLight,
                        focusedBorderColor = Primary
                    ),
                    shape = RoundedCornerShape(Dimens.radiusSmall)
                )

                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                // Phone Number Field
                OutlinedTextField(
                    value = if (isEditMode) editedPhoneNumber else (contact.phoneNumber ?: ""),
                    onValueChange = { editedPhoneNumber = it },
                    readOnly = !isEditMode,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderLight,
                        focusedBorderColor = Primary
                    ),
                    shape = RoundedCornerShape(Dimens.radiusSmall)
                )

                if (!isEditMode) {
                    Spacer(modifier = Modifier.height(Dimens.spaceXLarge))

                    // Save to Phone Button
                    OutlinedButton(
                        onClick = {
                            if (!isSavedToPhone) {
                                showSuccessMessage = true
                                onSaveToPhone()
                            }
                        },
                        enabled = !isSavedToPhone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.buttonHeight),
                        shape = RoundedCornerShape(Dimens.radiusXXLarge),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isSavedToPhone) Disabled else TextPrimary,
                            disabledContentColor = Disabled
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            Dimens.borderWidth,
                            if (isSavedToPhone) DisabledBorder else TextPrimary
                        )
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isSavedToPhone) R.drawable.savecontactfull else R.drawable.savecontact
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.iconMedium),
                            tint = if (isSavedToPhone) Disabled else TextPrimary
                        )
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                        Text(
                            text = "Save to My Phone Contact",
                            style = CustomTextStyles.saveButton.copy(
                                color = if (isSavedToPhone) Disabled else TextPrimary
                            )
                        )
                    }

                    // Info message when saved
                    if (isSavedToPhone) {
                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.info),
                                contentDescription = null,
                                modifier = Modifier.size(Dimens.iconLarge),
                                contentScale = ContentScale.None
                            )
                            Spacer(modifier = Modifier.width(Dimens.spaceXSmall))
                            Text(
                                text = "This contact is already saved your phone.",
                                style = CustomTextStyles.infoMessage
                            )
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePhotoSheet(
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.spaceMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                    
                    TextButton(
                        onClick = {
                            contact.id?.let { contactId ->
                                onUpdateContact(
                                    contactId,
                                    editedFirstName,
                                    editedLastName,
                                    editedPhoneNumber
                                )
                            }
                        }
                    ) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Dimens.spacerHeight19))
                
                // Profile Photo
                Box(
                    modifier = Modifier
                        .size(Dimens.avatarLarge)
                        .align(Alignment.CenterHorizontally)
                ) {
                    val photoUri = if (selectedPhotoUri != null) {
                        selectedPhotoUri
                    } else {
                        contact.photoUri?.let { Uri.parse(it) }
                    }
                    
                    if (photoUri != null) {
                        SubcomposeAsyncImage(
                            model = photoUri,
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(Dimens.avatarLarge)
                                .clip(CircleShape),
                            loading = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(Dimens.avatarLarge)
                                )
                            },
                            error = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(Dimens.avatarLarge)
                                )
                            }
                        )
                    } else {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(Dimens.avatarLarge)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                
                // Change Photo Button
                TextButton(
                    onClick = onChangePhoto,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Change Photo",
                        style = CustomTextStyles.changePhotoButton
                    )
                }
                
                Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                
                // First Name Field
                OutlinedTextField(
                    value = editedFirstName,
                    onValueChange = { editedFirstName = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderLight,
                        focusedBorderColor = Primary
                    ),
                    shape = RoundedCornerShape(Dimens.radiusSmall)
                )
                
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                
                // Last Name Field
                OutlinedTextField(
                    value = editedLastName,
                    onValueChange = { editedLastName = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderLight,
                        focusedBorderColor = Primary
                    ),
                    shape = RoundedCornerShape(Dimens.radiusSmall)
                )
                
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                
                // Phone Number Field
                OutlinedTextField(
                    value = editedPhoneNumber,
                    onValueChange = { editedPhoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderLight,
                        focusedBorderColor = Primary
                    ),
                    shape = RoundedCornerShape(Dimens.radiusSmall)
                )
            }
        }
    }
}

@Composable
private fun ContactInitialComponent(
    initial: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Primary
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = White,
                fontSize = 60.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
