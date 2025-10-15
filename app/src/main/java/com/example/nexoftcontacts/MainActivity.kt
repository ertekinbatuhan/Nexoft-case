package com.example.nexoftcontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexoftcontacts.presentation.launcher.rememberPhotoLaunchers
import com.example.nexoftcontacts.presentation.navigation.ContactsNavigation
import com.example.nexoftcontacts.presentation.permissions.rememberPermissionHandler
import com.example.nexoftcontacts.presentation.event.ContactEvent
import com.example.nexoftcontacts.presentation.viewmodel.ContactViewModel
import com.example.nexoftcontacts.ui.theme.NexoftContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexoftContactsTheme {
                ContactsApp()
            }
        }
    }
}

@Composable
fun ContactsApp(
    viewModel: ContactViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val permissionHandler = rememberPermissionHandler(
        context = context,
        coroutineScope = coroutineScope
    )
    
    val photoLaunchers = rememberPhotoLaunchers(
        photoRepository = viewModel.photoRepository,
        onPhotoSelected = { uri ->
            viewModel.onEvent(ContactEvent.SetSelectedPhoto(uri))
        }
    )
    
    ContactsNavigation(
        viewModel = viewModel,
        context = context,
        coroutineScope = coroutineScope,
        permissionHandler = permissionHandler,
        photoLaunchers = photoLaunchers
    )
}