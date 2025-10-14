package com.example.nexoftcontacts.domain.usecase

import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.domain.repository.ContactRepository

class GetContactsUseCase(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<Contact>> {
        return contactRepository.getAllContacts(forceRefresh)
    }
}
