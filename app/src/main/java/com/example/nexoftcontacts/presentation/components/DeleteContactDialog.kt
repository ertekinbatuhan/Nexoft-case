package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexoftcontacts.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteContactDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundLight,
        shape = RoundedCornerShape(topStart = Dimens.radiusLarge, topEnd = Dimens.radiusLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceMedium)
                .padding(top = Dimens.spaceMedium, bottom = Dimens.spaceXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Delete Contact",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            
            Text(
                text = "Are you sure you want to delete this contact?",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall2, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // No button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.buttonHeight)
                        .border(
                            width = 1.dp,
                            color = TextPrimary,
                            shape = RoundedCornerShape(Dimens.radiusXLarge)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BackgroundLight,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(Dimens.radiusXLarge)
                ) {
                    Text(
                        text = "No",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary)
                    )
                }
                
                // Yes button
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.buttonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextPrimary,
                        contentColor = BackgroundLight
                    ),
                    shape = RoundedCornerShape(Dimens.radiusXLarge)
                ) {
                    Text(
                        text = "Yes",
                        style = MaterialTheme.typography.labelSmall.copy(color = BackgroundLight)
                    )
                }
            }
        }
    }
}

