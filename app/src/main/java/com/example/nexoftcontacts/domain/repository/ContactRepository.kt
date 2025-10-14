package com.example.nexoftcontacts.domain.repository

import com.example.nexoftcontacts.data.model.Contact

interface ContactRepository {
    suspend fun getAllContacts(forceRefresh: Boolean = false): Result<List<Contact>>
    suspend fun getContactById(id: String): Result<Contact>
    suspend fun createUser(contact: Contact): Result<Contact>
    suspend fun updateUser(contact: Contact): Result<Contact>
    suspend fun deleteUser(id: String): Result<Unit>
    suspend fun uploadImage(imageFile: java.io.File): Result<String>
}
