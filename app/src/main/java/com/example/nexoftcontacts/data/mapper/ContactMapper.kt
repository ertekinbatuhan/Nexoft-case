package com.example.nexoftcontacts.data.mapper

import com.example.nexoftcontacts.data.local.ContactEntity
import com.example.nexoftcontacts.data.model.Contact
import java.util.UUID

fun ContactEntity.toDomain(): Contact = Contact(
    id = id,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    photoUri = photoUri,
    isDeviceContact = isDeviceContact
)

fun Contact.toEntity(): ContactEntity = ContactEntity(
    id = id ?: UUID.randomUUID().toString(),
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    photoUri = photoUri,
    isDeviceContact = isDeviceContact,
    lastUpdated = System.currentTimeMillis()
)
