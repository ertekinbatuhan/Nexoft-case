package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nexoftcontacts.data.model.Contact

@Composable
fun ContactList(
    contacts: List<Contact>,
    searchQuery: String,
    onDeleteContact: (String) -> Unit,
    onContactClick: (Contact) -> Unit,
    onContactEditClick: (Contact) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isSearching = searchQuery.isNotBlank()
    val sortedContacts = contacts.sortedBy { it.firstName?.lowercase() ?: "" }
    
    println("DEBUG: Contacts count: ${contacts.size}")
    
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isSearching) {
            item {
                SearchResultsCard(
                    contacts = sortedContacts,
                    onDeleteContact = onDeleteContact,
                    onContactClick = onContactClick,
                    onContactEditClick = onContactEditClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        } else {
            // Show grouped contacts by letter when not searching
            val groupedContacts = sortedContacts
                .groupBy { it.firstName?.firstOrNull()?.uppercaseChar() ?: '#' }
            
            groupedContacts.forEach { (letter, contactsInGroup) ->
                item {
                    ContactSectionCard(
                        sectionLetter = letter.toString(),
                        contacts = contactsInGroup,
                        onDeleteContact = onDeleteContact,
                        onContactClick = onContactClick,
                        onContactEditClick = onContactEditClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
