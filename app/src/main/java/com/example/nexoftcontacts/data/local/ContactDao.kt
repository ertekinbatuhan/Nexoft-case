package com.example.nexoftcontacts.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDao {
    
    @Query("SELECT * FROM contacts ORDER BY firstName ASC")
    suspend fun getAllContacts(): List<ContactEntity>
    
    @Query("SELECT * FROM contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: String): ContactEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)
    
    @Update
    suspend fun updateContact(contact: ContactEntity)
    
    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun deleteContactById(contactId: String)
    
    @Query("DELETE FROM contacts")
    suspend fun clearCache()
}
