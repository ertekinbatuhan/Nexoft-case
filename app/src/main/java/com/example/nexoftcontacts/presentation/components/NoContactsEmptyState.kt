package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun NoContactsEmptyState(
    onCreateNewContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = Dimens.emptyStateIconTopPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.iconXXLarge)
                .background(BackgroundLight, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.frame),
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconXXLarge),
                tint = Disabled
            )
        }
        
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        
        Text(
            text = "No Contacts",
            fontSize = 24.sp,
            fontWeight = FontWeight(700),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
        
        Text(
            text = "Contacts you've added will appear here.",
            fontSize = 16.sp,
            fontWeight = FontWeight(500),
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 310.dp)
        )
        
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
        
        TextButton(
            onClick = onCreateNewContactClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Primary
            )
        ) {
            Text(
                text = "Create New Contact",
                fontSize = 16.sp,
                fontWeight = FontWeight(700),
                color = Primary,
                textAlign = TextAlign.Center
            )
        }
    }
}