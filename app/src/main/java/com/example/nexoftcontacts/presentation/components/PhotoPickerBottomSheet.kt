package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundLight,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceLarge)
                .padding(top = Dimens.spaceLarge, bottom = Dimens.spaceSmall)
        ) {
            // Camera Button
            OutlinedButton(
                onClick = onCameraClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                shape = RoundedCornerShape(Dimens.radiusXXLarge),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                border = BorderStroke(
                    width = Dimens.borderWidth,
                    color = TextPrimary
                ),
                contentPadding = PaddingValues(Dimens.spaceSmall3)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Camera",
                    modifier = Modifier.size(Dimens.spaceLarge)
                )
                Spacer(modifier = Modifier.width(Dimens.spaceSmall2))
                Text(
                    text = "Camera",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            // Gallery Button
            OutlinedButton(
                onClick = onGalleryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                shape = RoundedCornerShape(Dimens.radiusXXLarge),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                border = BorderStroke(
                    width = Dimens.borderWidth,
                    color = TextPrimary
                ),
                contentPadding = PaddingValues(Dimens.spaceSmall3)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.gallery),
                    contentDescription = "Gallery",
                    modifier = Modifier.size(Dimens.spaceLarge)
                )
                Spacer(modifier = Modifier.width(Dimens.spaceSmall2))
                Text(
                    text = "Gallery",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            // Cancel Button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                contentPadding = PaddingValues(
                    horizontal = Dimens.spaceLarge,
                    vertical = Dimens.spaceSmall3
                )
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }
}
