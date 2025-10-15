package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun ContactFormFields(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
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
            onValueChange = onLastNameChange,
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

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() || it == '+' || it == '-' || it == ' ' || it == '(' || it == ')' }) {
                    onPhoneNumberChange(newValue)
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
