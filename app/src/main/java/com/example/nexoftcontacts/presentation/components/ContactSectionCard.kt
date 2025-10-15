package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.ui.theme.*

@Composable
fun ContactSectionCard(
    sectionLetter: String,
    contacts: List<Contact>,
    onDeleteContact: (String) -> Unit,
    onContactClick: (Contact) -> Unit,
    onContactEditClick: (Contact) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = BackgroundLight
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimens.elevationSmall
        ),
        shape = RoundedCornerShape(Dimens.radiusMedium)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Section header
            Text(
                text = sectionLetter,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = Dimens.spaceMedium, top = Dimens.spaceMedium, bottom = Dimens.spaceSmall)
            )
            
            // Divider after section header
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Dimens.spaceMedium),
                thickness = 0.5.dp,
                color = BorderLight
            )
            
            // Contacts in this section
            contacts.forEachIndexed { index, contact ->
                ContactRow(
                    contact = contact,
                    onDeleteContact = onDeleteContact,
                    onContactClick = onContactClick,
                    onContactEditClick = onContactEditClick,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Add divider between contacts (except for the last one)
                if (index < contacts.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Dimens.spaceMedium),
                        thickness = 0.5.dp,
                        color = BorderLight
                    )
                }
            }
        }
    }
}
