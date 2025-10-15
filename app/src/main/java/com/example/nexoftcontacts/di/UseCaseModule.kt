package com.example.nexoftcontacts.di

import com.example.nexoftcontacts.domain.repository.ContactRepository
import com.example.nexoftcontacts.domain.usecase.*
import com.example.nexoftcontacts.data.repository.PhotoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideGetContactsUseCase(repository: ContactRepository): GetContactsUseCase {
        return GetContactsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideCreateContactUseCase(repository: ContactRepository): CreateContactUseCase {
        return CreateContactUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideUpdateContactUseCase(repository: ContactRepository): UpdateContactUseCase {
        return UpdateContactUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideDeleteContactUseCase(repository: ContactRepository): DeleteContactUseCase {
        return DeleteContactUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun providePhotoPickerUseCase(photoRepository: PhotoRepositoryImpl): PhotoPickerUseCase {
        return PhotoPickerUseCase(photoRepository)
    }
}
