package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun ContactDetailsFormFields(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    isEditMode: Boolean,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onPhoneNumberChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // First Name Field
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
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
            value = lastName,
            onValueChange = onLastNameChange,
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
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            readOnly = !isEditMode,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderLight,
                focusedBorderColor = Primary
            ),
            shape = RoundedCornerShape(Dimens.radiusSmall)
        )
    }
}
