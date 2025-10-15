package com.example.nexoftcontacts.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactRow(
    contact: Contact,
    onDeleteContact: (String) -> Unit,
    onContactClick: (Contact) -> Unit,
    onContactEditClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    
    val offsetX = remember { Animatable(0f) }
    val maxSwipeDistance = with(density) { -120.dp.toPx() }
    
    if (showDeleteDialog) {
        DeleteContactDialog(
            onDismiss = { 
                showDeleteDialog = false
                coroutineScope.launch {
                    offsetX.animateTo(0f)
                }
            },
            onConfirm = { 
                contact.id?.let { onDeleteContact(it) }
                coroutineScope.launch {
                    offsetX.animateTo(0f)
                }
            }
        )
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .background(SwipeEdit)
                    .clickable {
                        onContactEditClick(contact)
                        coroutineScope.launch {
                            offsetX.animateTo(0f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    tint = BackgroundLight,
                    modifier = Modifier.size(Dimens.iconSmall)
                )
            }
            
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .fillMaxHeight()
                    .background(Error)
                    .clickable {
                        showDeleteDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete",
                    tint = BackgroundLight,
                    modifier = Modifier.size(Dimens.iconMedium)
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (offsetX.value < maxSwipeDistance / 2) {
                                    offsetX.animateTo(maxSwipeDistance)
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                val newValue = (offsetX.value + dragAmount).coerceIn(maxSwipeDistance, 0f)
                                offsetX.snapTo(newValue)
                            }
                        }
                    )
                }
                .background(BackgroundLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        if (offsetX.value == 0f) {
                            onContactClick(contact)
                        } else {
                            coroutineScope.launch {
                                offsetX.animateTo(0f)
                            }
                        }
                    }
                    .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
