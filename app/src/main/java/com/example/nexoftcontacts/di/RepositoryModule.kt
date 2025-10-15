package com.example.nexoftcontacts.di

import android.content.Context
import com.example.nexoftcontacts.data.repository.ContactRepositoryImpl
import com.example.nexoftcontacts.data.repository.PhotoRepositoryImpl
import com.example.nexoftcontacts.domain.repository.ContactRepository
import com.example.nexoftcontacts.data.remote.ContactApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideContactRepository(
        @ApplicationContext context: Context,
        apiService: ContactApiService
    ): ContactRepository {
        return ContactRepositoryImpl(context, apiService)
    }
    
    @Provides
    @Singleton
    fun providePhotoRepository(
        @ApplicationContext context: Context
    ): PhotoRepositoryImpl {
        return PhotoRepositoryImpl(context)
    }
}
