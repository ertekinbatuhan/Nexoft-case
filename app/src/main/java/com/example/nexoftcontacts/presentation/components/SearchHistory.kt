package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.nexoftcontacts.ui.theme.BorderLight
import com.example.nexoftcontacts.ui.theme.CustomTextStyles
import com.example.nexoftcontacts.ui.theme.Dimens
import com.example.nexoftcontacts.ui.theme.TextTertiary
import com.example.nexoftcontacts.ui.theme.White

@Composable
fun SearchHistory(
    searchHistory: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onRemoveHistoryItem: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (searchHistory.isEmpty()) return
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header with "SEARCH HISTORY" and "Clear All" - OUTSIDE the card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceXLarge, vertical = Dimens.spaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SEARCH HISTORY",
                style = CustomTextStyles.searchHistoryTitle
            )
            
            TextButton(
                onClick = onClearAll,
                contentPadding = PaddingValues(horizontal = Dimens.spaceSmall, vertical = Dimens.spaceXSmall)
            ) {
                Text(
                    text = "Clear All",
                    style = CustomTextStyles.clearAllButton
                )
            }
        }
        
        // Card with history items
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceMedium),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Dimens.elevationSmall
            ),
            shape = RoundedCornerShape(Dimens.radiusMedium)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // History items
                searchHistory.forEachIndexed { index, query ->
                    SearchHistoryItem(
                        query = query,
                        onClick = { onHistoryItemClick(query) },
                        onRemove = { onRemoveHistoryItem(query) }
                    )
                    
                    // Add divider between items (except for the last one)
                    if (index < searchHistory.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = Dimens.spaceMedium),
                            thickness = Dimens.borderWidth / 2,
                            color = BorderLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHistoryItem(
    query: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = TextTertiary,
            modifier = Modifier
                .size(Dimens.iconSmall)
                .clickable(onClick = onRemove)
        )
        
        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
        
        Text(
            text = query,
            style = CustomTextStyles.searchHistoryItem,
            modifier = Modifier.weight(1f)
        )
    }
}
