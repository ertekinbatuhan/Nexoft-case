package com.example.nexoftcontacts.domain.usecase

import com.example.nexoftcontacts.domain.repository.ContactRepository

class DeleteContactUseCase(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return contactRepository.deleteUser(id)
    }
}
