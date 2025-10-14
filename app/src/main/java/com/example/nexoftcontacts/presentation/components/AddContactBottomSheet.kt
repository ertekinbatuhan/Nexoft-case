package com.example.nexoftcontacts.presentation.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.nexoftcontacts.R

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
        containerColor = Color.White,
        modifier = modifier,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    // Clear form when canceling
                    firstName = ""
                    lastName = ""
                    phoneNumber = ""
                    onDismiss()
                }) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF0075FF)
                        ),
                        modifier = Modifier
                            .width(51.dp)
                            .height(20.dp)
                    )
                }
                
                Text(
                    text = "New Contact",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF202020)
                    ),
                    modifier = Modifier
                        .width(126.dp)
                        .height(25.dp)
                )
                
                TextButton(
                    onClick = {
                        if (isDoneEnabled) {
                            onSave(firstName, lastName, phoneNumber)
                            // Clear form after saving
                            firstName = ""
                            lastName = ""
                            phoneNumber = ""
                            onDismiss()
                        }
                    },
                    enabled = isDoneEnabled
                ) {
                    Text(
                        text = "Done",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(700),
                            color = if (isDoneEnabled) Color(0xFF0075FF) else Color.Gray
                        ),
                        modifier = Modifier
                            .width(41.dp)
                            .height(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedPhotoUri != null) {
                    // Show selected photo
                    Box(
                        modifier = Modifier
                            .padding(1.83465.dp)
                            .width(96.dp)
                            .height(95.90683.dp)
                            .clickable { 
                                showPhotoPickerSheet = true
                            }
                    ) {
                        SubcomposeAsyncImage(
                            model = selectedPhotoUri,
                            contentDescription = "Selected photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                } else {
                    // Show default frame
                    Image(
                        painter = painterResource(id = R.drawable.frame),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        modifier = Modifier
                            .padding(1.83465.dp)
                            .width(96.dp)
                            .height(95.90683.dp)
                            .clickable { 
                                showPhotoPickerSheet = true
                            }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = if (selectedPhotoUri != null) "Change Photo" else "Add Photo",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF0075FF),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { 
                            showPhotoPickerSheet = true
                        }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "First Name",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(600),
                                color = Color(0xFF888888)
                            )
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Last Name",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(600),
                                color = Color(0xFF888888)
                            )
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '+' || it == '-' || it == ' ' || it == '(' || it == ')' }) {
                            phoneNumber = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Phone Number",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(600),
                                color = Color(0xFF888888)
                            )
                        )
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF0075FF)
                    ),
                    singleLine = true
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoPickerBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            // Camera Button
            OutlinedButton(
                onClick = onCameraClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(64.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF0F172A)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF0F172A)
                ),
                contentPadding = PaddingValues(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Camera",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Camera",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Gallery Button
            OutlinedButton(
                onClick = onGalleryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(64.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF0F172A)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF0F172A)
                ),
                contentPadding = PaddingValues(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.gallery),
                    contentDescription = "Gallery",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Gallery",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Cancel Button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF0075FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

