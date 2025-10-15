package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.nexoftcontacts.R
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun ContactDetailsHeader(
    isEditMode: Boolean,
    onCancelEdit: () -> Unit,
    onDoneEdit: () -> Unit,
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isEditMode) {
        // Edit mode header
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = Dimens.spaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancelEdit) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.displaySmall
                )
            }

            Text(
                text = "Edit Contact",
                style = MaterialTheme.typography.headlineLarge
            )

            TextButton(onClick = onDoneEdit) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
    } else {
        // View mode header with menu icon
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = Dimens.spaceXLarge)
        ) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = IconBlack
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onDismissMenu,
                    offset = DpOffset(x = 0.dp, y = -Dimens.spaceSmall),
                    modifier = Modifier.background(White)
                ) {
                    // Edit option
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Edit",
                                    style = DropdownTextStyles.menuItem.copy(
                                        color = TextPrimary
                                    )
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.edit),
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier
                                        .size(Dimens.iconLarge)
                                        .padding(Dimens.spaceXSmall)
                                )
                            }
                        },
                        onClick = onEditClick
                    )

                    // Delete option
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Delete",
                                    style = DropdownTextStyles.menuItem.copy(
                                        color = Error
                                    )
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.delete),
                                    contentDescription = null,
                                    tint = Error,
                                    modifier = Modifier
                                        .size(Dimens.iconLarge)
                                        .padding(start = 3.dp, top = 3.dp, end = 3.dp, bottom = 3.dp)
                                )
                            }
                        },
                        onClick = onDeleteClick
                    )
                }
            }
        }
    }
}
