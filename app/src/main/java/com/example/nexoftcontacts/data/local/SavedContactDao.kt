package com.example.nexoftcontacts.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedContactDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedContact(contact: SavedContactEntity)
    
    @Query("SELECT * FROM saved_contacts WHERE contactId = :contactId LIMIT 1")
    suspend fun getSavedContact(contactId: String): SavedContactEntity?
    
    @Query("SELECT * FROM saved_contacts")
    fun getAllSavedContacts(): Flow<List<SavedContactEntity>>
    
    @Query("DELETE FROM saved_contacts WHERE contactId = :contactId")
    suspend fun deleteSavedContact(contactId: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM saved_contacts WHERE contactId = :contactId)")
    suspend fun isContactSaved(contactId: String): Boolean
}
