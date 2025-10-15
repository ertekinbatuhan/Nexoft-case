package com.example.nexoftcontacts.di

import com.example.nexoftcontacts.data.remote.ContactApiService
import com.example.nexoftcontacts.data.remote.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideContactApiService(): ContactApiService {
        return com.example.nexoftcontacts.data.remote.NetworkModule.contactApiService
    }
}
