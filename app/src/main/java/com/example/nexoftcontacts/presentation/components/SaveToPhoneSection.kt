package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun SaveToPhoneSection(
    isSavedToPhone: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Save to Phone Button
        OutlinedButton(
            onClick = onSaveClick,
            enabled = !isSavedToPhone,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonHeight),
            shape = RoundedCornerShape(Dimens.radiusXXLarge),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isSavedToPhone) Disabled else TextPrimary,
                disabledContentColor = Disabled
            ),
            border = BorderStroke(
                Dimens.borderWidth,
                if (isSavedToPhone) DisabledBorder else TextPrimary
            )
        ) {
            Icon(
                painter = painterResource(
                    id = if (isSavedToPhone) R.drawable.savecontactfull else R.drawable.savecontact
                ),
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconMedium),
                tint = if (isSavedToPhone) Disabled else TextPrimary
            )
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            Text(
                text = "Save to My Phone Contact",
                style = CustomTextStyles.saveButton.copy(
                    color = if (isSavedToPhone) Disabled else TextPrimary
                )
            )
        }

        // Info message when saved
        if (isSavedToPhone) {
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.info),
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconLarge),
                    contentScale = ContentScale.None
                )
                Spacer(modifier = Modifier.width(Dimens.spaceXSmall))
                Text(
                    text = "This contact is already saved your phone.",
                    style = CustomTextStyles.infoMessage
                )
            }
        }
    }
}
