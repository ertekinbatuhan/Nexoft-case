package com.example.nexoftcontacts.presentation.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.presentation.components.PhotoPickerBottomSheet
import com.example.nexoftcontacts.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    selectedPhotoUri: Uri? = null,
    onDismiss: () -> Unit,
    onSave: (firstName: String, lastName: String, phoneNumber: String) -> Unit,
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.displaySmall
                    )
                }

                Text(
                    text = "New Contact",
                    style = MaterialTheme.typography.headlineLarge
                )

                TextButton(
                    onClick = {
                        if (isDoneEnabled) {
                            onSave(firstName, lastName, phoneNumber)
                        }
                    },
                    enabled = isDoneEnabled
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(Dimens.spaceMedium),
                            strokeWidth = 2.dp,
                            color = Primary
                        )
                    } else {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.displayMedium.copy(
                                color = if (isDoneEnabled) Primary else Disabled
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spaceLarge))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedPhotoUri != null) {
                    // Show selected photo
                    Box(
                        modifier = Modifier
                            .size(Dimens.iconHuge)
                            .clickable {
                                showPhotoPickerSheet = true
                            }
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(selectedPhotoUri)
                                .crossfade(true)
                                .build(),
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
                            .size(Dimens.iconHuge)
                            .clickable {
                                showPhotoPickerSheet = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                Text(
                    text = if (selectedPhotoUri != null) "Change Photo" else "Add Photo",
                    style = CustomTextStyles.changePhotoButton,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = Dimens.spaceMedium)
                        .clickable {
                            showPhotoPickerSheet = true
                        }
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceXLarge))

            // TextFields
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "First Name",
                        style = MaterialTheme.typography.displayLarge
                    )
                },
                shape = RoundedCornerShape(Dimens.radiusSmall),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderLight,
                    focusedBorderColor = Primary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Dimens.spaceSmall2))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Last Name",
                        style = MaterialTheme.typography.displayLarge
                    )
                },
                shape = RoundedCornerShape(Dimens.radiusSmall),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderLight,
                    focusedBorderColor = Primary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Dimens.spaceSmall2))

            Spacer(modifier = Modifier.height(Dimens.spaceSmall2))

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
                        style = MaterialTheme.typography.displayLarge
                    )
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(Dimens.radiusSmall),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderLight,
                    focusedBorderColor = Primary
                ),
                singleLine = true
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
