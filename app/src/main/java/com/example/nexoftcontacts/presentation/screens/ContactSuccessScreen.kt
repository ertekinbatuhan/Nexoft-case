package com.example.nexoftcontacts.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nexoftcontacts.presentation.components.DoneAnimation
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
                .background(Color.White)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Success Animation
                DoneAnimation(
                    modifier = Modifier
                        .width(96.dp)
                        .height(95.74.dp),
                    isPlaying = true,
                    iterations = 1
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // "All Done!" text
                Text(
                    text = "All Done!",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF202020),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(310.dp)
                        .height(30.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // "New contact saved 🎉" text
                Text(
                    text = "New contact saved 🎉",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF3D3D3D),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(310.dp)
                        .height(20.dp)
                )
            }
        }
    }
}
