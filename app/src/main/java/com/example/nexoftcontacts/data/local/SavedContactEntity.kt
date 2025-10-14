package com.example.nexoftcontacts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_contacts")
data class SavedContactEntity(
    @PrimaryKey
    val contactId: String,
    val savedAt: Long = System.currentTimeMillis()
)
