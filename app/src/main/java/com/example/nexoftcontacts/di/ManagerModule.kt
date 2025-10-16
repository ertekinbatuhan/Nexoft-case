package com.example.nexoftcontacts.di

import android.content.Context
import com.example.nexoftcontacts.domain.manager.ContactPhotoHandler
import com.example.nexoftcontacts.domain.manager.ContactSearchHandler
import com.example.nexoftcontacts.domain.manager.ContactStateManager
import com.example.nexoftcontacts.domain.repository.ContactRepository
import com.example.nexoftcontacts.domain.usecase.PhotoPickerUseCase
import com.example.nexoftcontacts.utils.SearchHistoryManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {
    
    @Provides
    @Singleton
    fun provideContactStateManager(): ContactStateManager {
        return ContactStateManager()
    }
    
    @Provides
    @Singleton
    fun provideContactPhotoHandler(
        repository: ContactRepository
    ): ContactPhotoHandler {
        return ContactPhotoHandler(repository)
    }
    
    @Provides
    @Singleton
    fun provideSearchHistoryManager(
        @ApplicationContext context: Context
    ): SearchHistoryManager {
        return SearchHistoryManager(context)
    }
    
    @Provides
    @Singleton
    fun provideContactSearchHandler(
        searchHistoryManager: SearchHistoryManager
    ): ContactSearchHandler {
        return ContactSearchHandler(searchHistoryManager)
    }
}
