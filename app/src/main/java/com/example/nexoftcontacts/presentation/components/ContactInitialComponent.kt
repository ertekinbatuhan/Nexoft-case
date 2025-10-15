package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.nexoftcontacts.ui.theme.BackgroundLight
import com.example.nexoftcontacts.ui.theme.Primary
import com.example.nexoftcontacts.ui.theme.White

@Composable
fun ContactInitialComponent(
    initial: String,
    modifier: Modifier = Modifier,
    isLargeAvatar: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Primary
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isLargeAvatar) White else BackgroundLight,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (isLargeAvatar) 60.sp else MaterialTheme.typography.bodyLarge.fontSize
                )
            )
        }
    }
}
