package com.example.nexoftcontacts.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ContactEntity::class,
        SavedContactEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ContactDatabase : RoomDatabase() {
    
    abstract fun contactDao(): ContactDao
    abstract fun savedContactDao(): SavedContactDao
    
    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null
        
        fun getDatabase(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
