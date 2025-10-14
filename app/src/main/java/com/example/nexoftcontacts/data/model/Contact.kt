package com.example.nexoftcontacts.data.model

data class Contact(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val photoUri: String? = null,
    val isDeviceContact: Boolean = false
) {
    val fullName: String
        get() = "${firstName ?: ""} ${lastName ?: ""}".trim()
}

