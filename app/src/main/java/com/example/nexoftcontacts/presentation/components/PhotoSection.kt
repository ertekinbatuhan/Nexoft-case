package com.example.nexoftcontacts.presentation.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun PhotoSection(
    selectedPhotoUri: Uri?,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    photoSize: Dp = Dimens.iconHuge,
    initial: String? = null
) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedPhotoUri != null) {
            // Show selected photo
            Box(
                modifier = Modifier
                    .size(photoSize)
                    .clickable { onPhotoClick() }
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
            // Show initial component or default frame
            if (initial != null) {
                ContactInitialComponent(
                    initial = initial,
                    modifier = Modifier
                        .size(photoSize)
                        .clickable { onPhotoClick() },
                    isLargeAvatar = true
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.frame),
                    contentDescription = "image description",
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .size(photoSize)
                        .clickable { onPhotoClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spaceSmall))

        Text(
            text = if (selectedPhotoUri != null) "Change Photo" else "Add Photo",
            style = if (isEditMode) CustomTextStyles.changePhotoButton else MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = Dimens.spaceMedium)
                .clickable { onPhotoClick() }
        )
    }
}
