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
import com.example.nexoftcontacts.presentation.components.SearchHistory
import com.example.nexoftcontacts.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    contacts: List<com.example.nexoftcontacts.data.model.Contact>,
    searchQuery: String = "",
    searchHistory: List<String> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    showDeleteSuccess: Boolean = false,
    showUpdateSuccess: Boolean = false,
    onAddContactClick: () -> Unit,
    onRefresh: () -> Unit = {},
    onErrorDismiss: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onSearchFocusChanged: (Boolean) -> Unit = {},
    onHistoryItemClick: (String) -> Unit = {},
    onRemoveHistoryItem: (String) -> Unit = {},
    onClearSearchHistory: () -> Unit = {},
    onDeleteContact: (String) -> Unit = {},
    onDeleteSuccessDismiss: () -> Unit = {},
    onUpdateSuccessDismiss: () -> Unit = {},
    onContactClick: (com.example.nexoftcontacts.data.model.Contact) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSearchFocused by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contacts",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = onAddContactClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Primary,
                            contentColor = BackgroundLight
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact",
                            modifier = Modifier.size(Dimens.iconLarge)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundGray
                )
            )
        },
        containerColor = BackgroundGray
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
                    onFocusChanged = { focused ->
                        isSearchFocused = focused
                        onSearchFocusChanged(focused)
                    },
                    placeholder = "Search by name",
                    modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
                )
                
                // Show search history when search is focused and empty
                if (isSearchFocused && searchQuery.isEmpty() && searchHistory.isNotEmpty()) {
                    SearchHistory(
                        searchHistory = searchHistory,
                        onHistoryItemClick = { query ->
                            onHistoryItemClick(query)
                        },
                        onRemoveHistoryItem = onRemoveHistoryItem,
                        onClearAll = onClearSearchHistory,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (contacts.isEmpty()) {
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
                onDismiss = onDeleteSuccessDismiss,
                message = "User is deleted!"
            )
            
            DeleteSuccessSnackbar(
                showSnackbar = showUpdateSuccess,
                onDismiss = onUpdateSuccessDismiss,
                message = "User is updated!"
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

@Composable
private fun ContactList(
    contacts: List<com.example.nexoftcontacts.data.model.Contact>,
    searchQuery: String,
    onDeleteContact: (String) -> Unit,
    onContactClick: (com.example.nexoftcontacts.data.model.Contact) -> Unit,
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

@Composable
private fun SearchResultsCard(
    contacts: List<com.example.nexoftcontacts.data.model.Contact>,
    onDeleteContact: (String) -> Unit,
    onContactClick: (com.example.nexoftcontacts.data.model.Contact) -> Unit,
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
            // "TOP NAME MATCH(ES)" header - singular/plural based on count
            Text(
                text = if (contacts.size == 1) "TOP NAME MATCH" else "TOP NAME MATCHES",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = Dimens.spaceMedium, top = Dimens.spaceMedium, bottom = Dimens.spaceSmall)
            )
            
            // Divider after header
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Dimens.spaceMedium),
                thickness = 0.5.dp,
                color = BorderLight
            )
            
            // All search results in one list
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
                        modifier = Modifier.padding(horizontal = Dimens.spaceMedium),
                        thickness = 0.5.dp,
                        color = BorderLight
                    )
                }
            }
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
                .background(BackgroundLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onContactClick(contact) }
                    .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
            // Profile photo with badge
            Box {
                if (contact.photoUri != null) {
                    SubcomposeAsyncImage(
                        model = contact.photoUri,
                        contentDescription = "${contact.fullName} photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(Dimens.avatarSmall)
                            .clip(CircleShape),
                        loading = {
                            ContactInitialComponent(
                                initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                modifier = Modifier.size(Dimens.avatarSmall)
                            )
                        },
                        error = {
                            ContactInitialComponent(
                                initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                modifier = Modifier.size(Dimens.avatarSmall)
                            )
                        }
                    )
                } else {
                    ContactInitialComponent(
                        initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        modifier = Modifier.size(Dimens.avatarSmall)
                    )
                }
                
                // Phone badge if contact is saved to device
                if (contact.isDeviceContact) {
                    Icon(
                        painter = painterResource(id = R.drawable.telephone),
                        contentDescription = "Device Contact",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(Dimens.iconSmall)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = contact.phoneNumber ?: "No phone number",
                    style = MaterialTheme.typography.bodyMedium
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
            // Mavi düzenleme butonu
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
            
            // Kırmızı silme butonu
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

@Composable
private fun ContactItem(
    contact: com.example.nexoftcontacts.data.model.Contact,
    sectionLetter: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = Dimens.spaceMedium, vertical = 2.dp),
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
            // Section letter header
            sectionLetter?.let { letter ->
                Text(
                    text = letter,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = Dimens.spaceMedium, top = Dimens.spaceMedium, bottom = Dimens.spaceSmall)
                )
            }
            
            // Contact row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimens.spaceMedium,
                        end = Dimens.spaceMedium,
                        bottom = Dimens.spaceMedium,
                        top = if (sectionLetter != null) 0.dp else Dimens.spaceMedium
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
            if (contact.photoUri != null) {
                SubcomposeAsyncImage(
                    model = contact.photoUri,
                    contentDescription = "${contact.fullName} photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(Dimens.avatarSmall)
                        .clip(CircleShape),
                    loading = {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(Dimens.avatarSmall)
                        )
                    },
                    error = {
                        ContactInitialComponent(
                            initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            modifier = Modifier.size(Dimens.avatarSmall)
                        )
                    }
                )
            } else {
                ContactInitialComponent(
                    initial = contact.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    modifier = Modifier.size(Dimens.avatarSmall)
                )
            }
            
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = contact.phoneNumber ?: "No phone number",
                    style = MaterialTheme.typography.bodyMedium
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
        color = Primary
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = BackgroundLight,
                        fontWeight = FontWeight.SemiBold
                    )
                )
        }
    }
}