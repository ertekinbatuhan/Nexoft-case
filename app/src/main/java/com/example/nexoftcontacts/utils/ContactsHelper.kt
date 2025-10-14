package com.example.nexoftcontacts.utils

import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import com.example.nexoftcontacts.data.local.ContactDatabase
import com.example.nexoftcontacts.data.local.SavedContactEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactsHelper {
    
    suspend fun saveContactToPhone(
        context: Context,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val ops = ArrayList<ContentProviderOperation>()
                
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build()
                )
                
                // Add name
                val fullName = "${firstName ?: ""} ${lastName ?: ""}".trim()
                if (fullName.isNotEmpty()) {
                    ops.add(
                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
                            .build()
                    )
                }
                
                // Add phone number
                if (!phoneNumber.isNullOrEmpty()) {
                    ops.add(
                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build()
                    )
                }
                
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    suspend fun markContactAsSaved(context: Context, contactId: String) {
        withContext(Dispatchers.IO) {
            val db = ContactDatabase.getDatabase(context)
            db.savedContactDao().insertSavedContact(SavedContactEntity(contactId))
        }
    }
    
    suspend fun isContactSavedToPhone(context: Context, contactId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val db = ContactDatabase.getDatabase(context)
            db.savedContactDao().isContactSaved(contactId)
        }
    }
    
    suspend fun removeSavedContact(context: Context, contactId: String) {
        withContext(Dispatchers.IO) {
            val db = ContactDatabase.getDatabase(context)
            db.savedContactDao().deleteSavedContact(contactId)
        }
    }
}
