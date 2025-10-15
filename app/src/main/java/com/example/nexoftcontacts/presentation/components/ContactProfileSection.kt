package com.example.nexoftcontacts.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun ContactProfileSection(
    photoUri: Uri?,
    initial: String,
    dominantColor: Color = Primary,
    onChangePhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLargeAvatar: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Photo
        Box(
            modifier = Modifier.size(Dimens.avatarLarge)
        ) {
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
                            initial = initial,
                            modifier = Modifier.size(Dimens.avatarLarge),
                            isLargeAvatar = isLargeAvatar
                        )
                    },
                    error = {
                        ContactInitialComponent(
                            initial = initial,
                            modifier = Modifier.size(Dimens.avatarLarge),
                            isLargeAvatar = isLargeAvatar
                        )
                    }
                )
            } else {
                ContactInitialComponent(
                    initial = initial,
                    modifier = Modifier.size(Dimens.avatarLarge),
                    isLargeAvatar = isLargeAvatar
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spaceSmall))

        // Change Photo Button
        TextButton(
            onClick = onChangePhotoClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Change Photo",
                style = CustomTextStyles.changePhotoButton
            )
        }
    }
}
