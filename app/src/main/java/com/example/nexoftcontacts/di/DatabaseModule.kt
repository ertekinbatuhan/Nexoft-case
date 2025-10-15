package com.example.nexoftcontacts.di

import android.content.Context
import com.example.nexoftcontacts.data.local.ContactDatabase
import com.example.nexoftcontacts.data.local.ContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideContactDatabase(
        @ApplicationContext context: Context
    ): ContactDatabase {
        return ContactDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideContactDao(database: ContactDatabase): ContactDao {
        return database.contactDao()
    }
}
