package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun SwipeBackground(
    dismissValue: SwipeToDismissBoxValue,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {

            Box(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .background(SwipeEdit)
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    tint = BackgroundLight,
                    modifier = Modifier.size(Dimens.iconSmall)
                )
            }

            Box(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .background(
                        color = Error,
                        shape = RoundedCornerShape(topEnd = Dimens.radiusMedium, bottomEnd = Dimens.radiusMedium)
                    )
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete",
                    tint = BackgroundLight,
                    modifier = Modifier.size(Dimens.iconMedium)
                )
            }
        }
    }
}
