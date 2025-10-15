package com.example.nexoftcontacts.presentation.permissions

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.example.nexoftcontacts.ui.theme.BackgroundLight
import com.example.nexoftcontacts.ui.theme.Primary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Centralized permission handling for the app
 */
@Composable
fun rememberPermissionHandler(
    context: Context,
    coroutineScope: CoroutineScope
): PermissionHandlerState {
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionDialogMessage by remember { mutableStateOf("") }
    
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            permissionDialogMessage = "Camera permission is required to take photos. Please enable it in your device settings."
            showPermissionDialog = true
        }
    }
    
    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            permissionDialogMessage = "Contacts permission is required to save contact to your phone. Please enable it in your device settings."
            showPermissionDialog = true
        }
    }
    
    // Permission dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = {
                Text(
                    text = "Permission Required",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = permissionDialogMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("OK", color = Primary)
                }
            },
            containerColor = BackgroundLight
        )
    }
    
    return PermissionHandlerState(
        cameraPermissionLauncher = cameraPermissionLauncher,
        contactsPermissionLauncher = contactsPermissionLauncher,
        requestCameraPermission = { onGranted ->
            coroutineScope.launch(Dispatchers.Main) {
                val hasPermission = withContext(Dispatchers.IO) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                }
                
                if (hasPermission) {
                    onGranted()
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        },
        requestContactsPermission = { onGranted ->
            coroutineScope.launch(Dispatchers.Main) {
                val hasPermission = withContext(Dispatchers.IO) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_CONTACTS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                }
                
                if (hasPermission) {
                    onGranted()
                } else {
                    contactsPermissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
                }
            }
        }
    )
}

data class PermissionHandlerState(
    val cameraPermissionLauncher: androidx.activity.compose.ManagedActivityResultLauncher<String, Boolean>,
    val contactsPermissionLauncher: androidx.activity.compose.ManagedActivityResultLauncher<String, Boolean>,
    val requestCameraPermission: (onGranted: () -> Unit) -> Unit,
    val requestContactsPermission: (onGranted: () -> Unit) -> Unit
)
