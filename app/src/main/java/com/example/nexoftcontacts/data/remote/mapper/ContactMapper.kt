package com.example.nexoftcontacts.data.remote.mapper

import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.data.remote.dto.*

fun UserDto.toDomainModel(): Contact {
    return Contact(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        photoUri = this.profileImageUrl
    )
}

fun UserData.toDomainModel(): Contact {
    return Contact(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        photoUri = this.profileImageUrl
    )
}


fun Contact.toCreateUserRequest(): CreateUserRequest {
    return CreateUserRequest(
        firstName = this.firstName ?: "",
        lastName = this.lastName ?: "",
        phoneNumber = this.phoneNumber ?: "",
        profileImageUrl = this.photoUri
    )
}
