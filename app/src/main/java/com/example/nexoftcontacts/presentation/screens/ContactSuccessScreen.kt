package com.example.nexoftcontacts.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nexoftcontacts.R
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
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Success Icon
                Image(
                    painter = painterResource(id = R.drawable.done),
                    contentDescription = "Success",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(96.dp)
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
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // "New contact saved 🎉" text
                Text(
                    text = "New contact saved 🎉",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF202020),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}
