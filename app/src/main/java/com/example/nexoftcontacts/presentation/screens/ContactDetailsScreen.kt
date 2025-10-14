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
    var dominantColor by remember { mutableStateOf(Color(0xFF0075FF)) }
    
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
        containerColor = Color.White,
        dragHandle = null,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Top section - Edit mode or View mode
                if (isEditMode) {
                    // Edit mode header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
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
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF0075FF)
                                )
                            )
                        }

                        Text(
                            text = "Edit Contact",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF202020)
                            )
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
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFF0075FF)
                                )
                            )
                        }
                    }
                } else {
                    // View mode header with menu icon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)
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
                                    tint = Color.Black
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                offset = DpOffset(x = 0.dp, y = (-8).dp),
                                modifier = Modifier
                                    .background(Color.White)
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
                                                fontSize = 16.sp,
                                                color = Color(0xFF202020)
                                            )
                                            Icon(
                                                painter = painterResource(id = R.drawable.edit),
                                                contentDescription = null,
                                                tint = Color(0xFF202020),
                                                modifier = Modifier.size(20.dp)
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
                                                fontSize = 16.sp,
                                                color = Color(0xFFFF0000)
                                            )
                                            Icon(
                                                painter = painterResource(id = R.drawable.delete),
                                                contentDescription = null,
                                                tint = Color(0xFFFF0000),
                                                modifier = Modifier.size(20.dp)
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

                Spacer(modifier = Modifier.height(19.dp))

                // Profile Photo
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    val photoUri = selectedPhotoUri ?: contact.photoUri?.let { Uri.parse(it) }
                    
                    if (photoUri != null) {
                        SubcomposeAsyncImage(
                            model = photoUri,
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(150.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = CircleShape,
                                    ambientColor = dominantColor.copy(alpha = 0.5f),
                                    spotColor = dominantColor.copy(alpha = 0.5f)
                                )
                                .clip(CircleShape),
                            loading = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(150.dp)
                                )
                            },
                            error = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                        )
                    } else {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF0075FF),
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // First Name Field
                OutlinedTextField(
                    value = if (isEditMode) editedFirstName else (contact.firstName ?: ""),
                    onValueChange = { editedFirstName = it },
                    readOnly = !isEditMode,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Last Name Field
                OutlinedTextField(
                    value = if (isEditMode) editedLastName else (contact.lastName ?: ""),
                    onValueChange = { editedLastName = it },
                    readOnly = !isEditMode,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone Number Field
                OutlinedTextField(
                    value = if (isEditMode) editedPhoneNumber else (contact.phoneNumber ?: ""),
                    onValueChange = { editedPhoneNumber = it },
                    readOnly = !isEditMode,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                if (!isEditMode) {
                    Spacer(modifier = Modifier.height(32.dp))

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
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isSavedToPhone) Color(0xFFD1D1D1) else Color(
                                0xFF202020
                            ),
                            disabledContentColor = Color(0xFFD1D1D1)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSavedToPhone) Color(0xFFE7E7E7) else Color(0xFF202020)
                        )
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isSavedToPhone) R.drawable.savecontactfull else R.drawable.savecontact
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (isSavedToPhone) Color(0xFFD1D1D1) else Color(0xFF202020)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save to My Phone Contact",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(600),
                                color = if (isSavedToPhone) Color(0xFFD1D1D1) else Color(0xFF202020)
                            )
                        )
                    }

                    // Info message when saved
                    if (isSavedToPhone) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.info),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.None
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "This contact is already saved your phone.",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF6D6D6D),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }

                // Success message snackbar
                if (showSuccessMessage) {
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(3000)
                        showSuccessMessage = false
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = Color(0xFF12B76A),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.g443),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "User is added yo your phone!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF12B76A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
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
        containerColor = Color.White,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header - Without title, just Cancel and Done buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Cancel",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF0075FF)
                            )
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
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF0075FF)
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(19.dp))
                
                // Profile Photo
                Box(
                    modifier = Modifier
                        .size(150.dp)
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
                                .size(150.dp)
                                .clip(CircleShape),
                            loading = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(150.dp)
                                )
                            },
                            error = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                        )
                    } else {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Change Photo Button
                TextButton(
                    onClick = onChangePhoto,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Change Photo",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF0075FF),
                            textAlign = TextAlign.Center
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // First Name Field
                OutlinedTextField(
                    value = editedFirstName,
                    onValueChange = { editedFirstName = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Last Name Field
                OutlinedTextField(
                    value = editedLastName,
                    onValueChange = { editedLastName = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Phone Number Field
                OutlinedTextField(
                    value = editedPhoneNumber,
                    onValueChange = { editedPhoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
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
        color = Color(0xFF0075FF)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontSize = 60.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
