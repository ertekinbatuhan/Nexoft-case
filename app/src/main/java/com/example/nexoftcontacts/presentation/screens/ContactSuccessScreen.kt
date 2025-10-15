package com.example.nexoftcontacts.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nexoftcontacts.presentation.components.DoneAnimation
import com.example.nexoftcontacts.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ContactSuccessScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto dismiss after 2 seconds
    LaunchedEffect(Unit) {
        delay(2000)
        onDismiss()
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(horizontal = Dimens.spaceXLarge),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = Dimens.successScreenTopPadding)
            ) {
                // Success Animation
                DoneAnimation(
                    modifier = Modifier.size(Dimens.animationSuccess),
                    isPlaying = true,
                    iterations = 1
                )
                
                Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                
                // "All Done!" text
                Text(
                    text = "All Done!",
                    style = CustomTextStyles.successScreenTitle
                )
                
                Spacer(modifier = Modifier.height(Dimens.spaceXSmall))
                
                // "New contact saved 🎉" text
                Text(
                    text = "New contact saved 🎉",
                    style = CustomTextStyles.successScreenSubtitle
                )
            }
        }
    }
}