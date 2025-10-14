package com.example.nexoftcontacts.domain.usecase

import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.domain.repository.ContactRepository

class UpdateContactUseCase(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(contact: Contact): Result<Contact> {
        return contactRepository.updateUser(contact)
    }
}
