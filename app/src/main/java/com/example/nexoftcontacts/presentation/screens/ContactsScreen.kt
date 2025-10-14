package com.example.nexoftcontacts.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import com.example.nexoftcontacts.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.nexoftcontacts.presentation.components.SearchBar
import com.example.nexoftcontacts.presentation.components.NoSearchResults
import com.example.nexoftcontacts.presentation.components.DeleteContactDialog
import com.example.nexoftcontacts.presentation.components.DeleteSuccessSnackbar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    contacts: List<com.example.nexoftcontacts.data.model.Contact>,
    searchQuery: String = "",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    showDeleteSuccess: Boolean = false,
    onAddContactClick: () -> Unit,
    onRefresh: () -> Unit = {},
    onErrorDismiss: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onDeleteContact: (String) -> Unit = {},
    onDeleteSuccessDismiss: () -> Unit = {},
    onContactClick: (com.example.nexoftcontacts.data.model.Contact) -> Unit = {},
    modifier: Modifier = Modifier
) {
    
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onClearSearch = onClearSearch,
                    placeholder = "Search by name",
                    modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
                )
                
                if (contacts.isEmpty()) {
                    if (searchQuery.isNotBlank()) {
                        NoSearchResults(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        NoContactsEmptyState(
                            onCreateNewContactClick = onAddContactClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                ContactList(
                    contacts = contacts,
                    searchQuery = searchQuery,
                    onDeleteContact = onDeleteContact,
                    onContactClick = onContactClick,
                    modifier = Modifier.fillMaxSize()
                )
                }
            }
            
            DeleteSuccessSnackbar(
                showSnackbar = showDeleteSuccess,
                onDismiss = onDeleteSuccessDismiss
            )
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
    onDeleteContact: (String) -> Unit,
    onContactClick: (com.example.nexoftcontacts.data.model.Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredContacts = if (searchQuery.isBlank()) {
        contacts
    } else {
        contacts.filter { contact ->
            contact.fullName.contains(searchQuery, ignoreCase = true) ||
            (contact.phoneNumber?.contains(searchQuery) ?: false)
        }
    }
    
    val groupedContacts = filteredContacts
        .sortedBy { it.firstName?.lowercase() ?: "" }
        .groupBy { it.firstName?.firstOrNull()?.uppercaseChar() ?: '#' }
    
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
                    onDeleteContact = onDeleteContact,
                    onContactClick = onContactClick,
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
    onDeleteContact: (String) -> Unit,
    onContactClick: (com.example.nexoftcontacts.data.model.Contact) -> Unit,
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
                    onDeleteContact = onDeleteContact,
                    onContactClick = onContactClick,
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactRow(
    contact: com.example.nexoftcontacts.data.model.Contact,
    onDeleteContact: (String) -> Unit,
    onContactClick: (com.example.nexoftcontacts.data.model.Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            // Her iki yönü de kabul et
            true
        },
        positionalThreshold = { it * 0.25f }
    )
    
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
            dismissState.reset()
        }
    }
    
    if (showDeleteDialog) {
        DeleteContactDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { 
                contact.id?.let { onDeleteContact(it) }
            }
        )
    }
    
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            SwipeBackground(
                dismissValue = dismissState.targetValue,
                onEditClick = { 
                    onContactClick(contact) 
                    coroutineScope.launch {
                        dismissState.reset() // Butona tıklayınca kapat
                    }
                },
                onDeleteClick = { 
                    showDeleteDialog = true
                    coroutineScope.launch {
                        dismissState.reset() // Butona tıklayınca kapat
                    }
                }
            )
        },
        enableDismissFromStartToEnd = true, // Sağa kaydırmayı etkinleştir
        enableDismissFromEndToStart = true, // Sola kaydırmayı etkinleştir
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onContactClick(contact) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    error = {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                )
            } else {
                ContactInitialComponent(
                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
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
                    text = contact.phoneNumber ?: "No phone number",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
            }
        }
    }
}

@Composable
private fun SwipeBackground(
    dismissValue: SwipeToDismissBoxValue,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val color = when (dismissValue) {
        SwipeToDismissBoxValue.EndToStart -> Color.Transparent
        else -> Color.Transparent
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Mavi düzenleme butonu
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF1EA7FF))
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            // Kırmızı silme butonu
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .background(
                        color = Color(0xFFFF0000),
                        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                    )
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
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
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    error = {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                )
            } else {
                ContactInitialComponent(
                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
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
                    text = contact.phoneNumber ?: "No phone number",
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