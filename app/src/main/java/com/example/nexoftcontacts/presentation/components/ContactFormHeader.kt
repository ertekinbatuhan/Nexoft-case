package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun ContactFormHeader(
    title: String,
    isDoneEnabled: Boolean,
    isLoading: Boolean = false,
    onCancel: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.displaySmall
            )
        }

        Text(
            text = title,
            style = if (title == "New Contact") {
                MaterialTheme.typography.headlineLarge
            } else if (title.isNotEmpty()) {
                CustomTextStyles.editContactTitle
            } else {
                // Empty title - just for spacing
                MaterialTheme.typography.headlineLarge
            },
            modifier = if (title.isEmpty()) Modifier.width(0.dp) else Modifier
        )

        TextButton(
            onClick = onDone,
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
}
