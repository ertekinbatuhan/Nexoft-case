package com.example.nexoftcontacts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val photoUri: String?,
    val isDeviceContact: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
