package com.example.nexoftcontacts.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    contacts: List<com.example.nexoftcontacts.data.model.Contact>,
    onAddContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contacts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W800,
                        color = Color(0xFF0F172A)
                    )
                },
                actions = {
                    IconButton(
                        onClick = onAddContactClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF0075FF),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF6F6F6)
                )
            )
        },
        containerColor = Color(0xFFF6F6F6)
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 10.dp),
                placeholder = {
                    Text(
                        text = "Search by name",
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF0075FF)
                ),
                singleLine = true
            )
            
            if (contacts.isEmpty()) {
                NoContactsEmptyState(
                    onCreateNewContactClick = onAddContactClick,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ContactList(
                    contacts = contacts,
                    searchQuery = searchQuery,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun NoContactsEmptyState(
    onCreateNewContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color(0xFFE8E8E8)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No Contacts",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Contacts you've added will appear here.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TextButton(
            onClick = onCreateNewContactClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF0075FF)
            )
        ) {
            Text(
                text = "Create New Contact",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ContactList(
    contacts: List<com.example.nexoftcontacts.data.model.Contact>,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val filteredContacts = if (searchQuery.isBlank()) {
        contacts
    } else {
        contacts.filter { contact ->
            contact.fullName.contains(searchQuery, ignoreCase = true) ||
            contact.phoneNumber.contains(searchQuery)
        }
    }
    
    val groupedContacts = filteredContacts
        .sortedBy { it.firstName.lowercase() }
        .groupBy { it.firstName.first().uppercaseChar() }
    
    println("DEBUG: Filtered contacts: ${filteredContacts.size}")
    println("DEBUG: Grouped contacts: ${groupedContacts.keys}")
    
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedContacts.forEach { (letter, contactsInGroup) ->
            item {
                ContactSectionCard(
                    sectionLetter = letter.toString(),
                    contacts = contactsInGroup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ContactSectionCard(
    sectionLetter: String,
    contacts: List<com.example.nexoftcontacts.data.model.Contact>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
            // Section header
            Text(
                text = sectionLetter,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            
            // Divider after section header
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color(0xFFE5E5E5)
            )
            
            // Contacts in this section
            contacts.forEachIndexed { index, contact ->
                ContactRow(
                    contact = contact,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Add divider between contacts (except for the last one)
                if (index < contacts.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFE5E5E5)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ContactRow(
    contact: com.example.nexoftcontacts.data.model.Contact,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (contact.photoUri != null) {
            SubcomposeAsyncImage(
                model = contact.photoUri,
                contentDescription = "${contact.fullName} photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                loading = {
                    ContactInitialComponent(
                        initial = contact.firstName.first().uppercaseChar().toString(),
                        modifier = Modifier.size(40.dp)
                    )
                },
                error = {
                    ContactInitialComponent(
                        initial = contact.firstName.first().uppercaseChar().toString(),
                        modifier = Modifier.size(40.dp)
                    )
                }
            )
        } else {
            ContactInitialComponent(
                initial = contact.firstName.first().uppercaseChar().toString(),
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contact.fullName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0F172A)
            )
            Text(
                text = contact.phoneNumber,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun ContactItem(
    contact: com.example.nexoftcontacts.data.model.Contact,
    sectionLetter: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 2.dp),
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
            // Section letter header
            sectionLetter?.let { letter ->
                Text(
                    text = letter,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            
            // Contact row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                        top = if (sectionLetter != null) 0.dp else 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
            if (contact.photoUri != null) {
                SubcomposeAsyncImage(
                    model = contact.photoUri,
                    contentDescription = "${contact.fullName} photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    loading = {
                        ContactInitialComponent(
                            initial = contact.firstName.first().uppercaseChar().toString(),
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    error = {
                        ContactInitialComponent(
                            initial = contact.firstName.first().uppercaseChar().toString(),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                )
            } else {
                ContactInitialComponent(
                    initial = contact.firstName.first().uppercaseChar().toString(),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
            }
        }
    }
}

@Composable
private fun ContactInitialComponent(
    initial: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color(0xFF0075FF)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
                Text(
                    text = initial,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
        }
    }
}