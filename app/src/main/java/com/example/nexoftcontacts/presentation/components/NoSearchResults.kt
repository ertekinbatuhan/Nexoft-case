package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
fun NoSearchResults(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = Dimens.emptyStateTopPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Icon with background circle
        Box(
            modifier = Modifier
                .size(Dimens.iconHuge)
                .background(Disabled, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconXXLarge2),
                tint = BackgroundLight
            )
        }
        
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        
        Text(
            text = "No Results",
            fontSize = 24.sp,
            fontWeight = FontWeight(700),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimens.spaceXSmall))
        
        Text(
            text = "The user you are looking for could not be found.",
            fontSize = 16.sp,
            fontWeight = FontWeight(500),
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 310.dp)
        )
    }
}
