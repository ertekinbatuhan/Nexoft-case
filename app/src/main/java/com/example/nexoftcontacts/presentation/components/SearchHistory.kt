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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SEARCH HISTORY",
                fontSize = 13.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF6B7280),
                letterSpacing = 0.5.sp
            )
            
            TextButton(
                onClick = onClearAll,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "Clear All",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF0075FF)
                )
            }
        }
        
        // Card with history items
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            ),
            shape = RoundedCornerShape(12.dp)
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
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFE5E5E5)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // X icon on the left
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = Color(0xFF4F4F4F),
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onRemove)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Text next to icon
        Text(
            text = query,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4F4F4F),
            modifier = Modifier.weight(1f)
        )
    }
}
