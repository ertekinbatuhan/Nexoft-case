package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.nexoftcontacts.ui.theme.CustomTextStyles
import com.example.nexoftcontacts.ui.theme.Dimens
import com.example.nexoftcontacts.ui.theme.Success
import com.example.nexoftcontacts.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun SuccessSnackbar(
    showSnackbar: Boolean,
    onDismiss: () -> Unit,
    message: String = "User is deleted!"
) {
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            delay(3000) 
            onDismiss()
        }
    }
    
    if (showSnackbar) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = Dimens.spaceXLarge,
                        start = Dimens.spaceMedium,
                        end = Dimens.spaceMedium
                    ),
                shape = RoundedCornerShape(Dimens.radiusMedium),
                color = White,
                shadowElevation = Dimens.elevationMedium
            ) {
                Row(
                    modifier = Modifier
                        .padding(Dimens.spaceMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall2)
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimens.iconLarge)
                            .background(
                                color = Success,
                                shape = RoundedCornerShape(Dimens.radiusMedium)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = White,
                            modifier = Modifier.size(Dimens.iconSmall)
                        )
                    }
                    
                    Text(
                        text = message,
                        style = CustomTextStyles.successMessage
                    )
                }
            }
        }
    }
}

