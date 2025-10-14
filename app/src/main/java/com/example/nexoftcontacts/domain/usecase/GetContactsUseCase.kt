package com.example.nexoftcontacts.domain.usecase

import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.domain.repository.ContactRepository

class GetContactsUseCase(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(): Result<List<Contact>> {
        return contactRepository.getAllContacts()
    }
}
