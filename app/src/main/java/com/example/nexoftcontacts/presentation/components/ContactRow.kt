package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactRow(
    contact: Contact,
    onDeleteContact: (String) -> Unit,
    onContactClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            // 
            true
        },
        positionalThreshold = { it * 0.25f }
    )
    
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
            dismissState.reset()
        }
    }
    
    if (showDeleteDialog) {
        DeleteContactDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { 
                contact.id?.let { onDeleteContact(it) }
            }
        )
    }
    
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            SwipeBackground(
                dismissValue = dismissState.targetValue,
                onEditClick = { 
                    onContactClick(contact) 
                    coroutineScope.launch {
                        dismissState.reset() // Butona tıklayınca kapat
                    }
                },
                onDeleteClick = { 
                    showDeleteDialog = true
                    coroutineScope.launch {
                        dismissState.reset() // Butona tıklayınca kapat
                    }
                }
            )
        },
        enableDismissFromStartToEnd = true, 
        enableDismissFromEndToStart = true, 
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onContactClick(contact) }
                    .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile photo with badge
                Box {
                    if (contact.photoUri != null) {
                        SubcomposeAsyncImage(
                            model = contact.photoUri,
                            contentDescription = "${contact.fullName} photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(Dimens.avatarSmall)
                                .clip(CircleShape),
                            loading = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(Dimens.avatarSmall)
                                )
                            },
                            error = {
                                ContactInitialComponent(
                                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    modifier = Modifier.size(Dimens.avatarSmall)
                                )
                            }
                        )
                    } else {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(Dimens.avatarSmall)
                        )
                    }
                    
                    // Phone badge if contact is saved to device
                    if (contact.isDeviceContact) {
                        Icon(
                            painter = painterResource(id = R.drawable.telephone),
                            contentDescription = "Device Contact",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(Dimens.iconSmall)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = contact.fullName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = contact.phoneNumber ?: "No phone number",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}