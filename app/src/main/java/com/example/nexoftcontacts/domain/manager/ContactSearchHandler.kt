package com.example.nexoftcontacts.domain.manager

import com.example.nexoftcontacts.data.model.Contact
import com.example.nexoftcontacts.utils.SearchHistoryManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactSearchHandler @Inject constructor(
    private val searchHistoryManager: SearchHistoryManager
) {
    
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    
    private var searchDebounceJob: Job? = null
    
    init {
        loadSearchHistory()
    }
    
    fun filterContacts(contacts: List<Contact>, query: String): List<Contact> {
        if (query.isBlank()) {
            return contacts
        }
        
        val lowercaseQuery = query.lowercase().trim()
        return contacts.filter { contact ->
            val fullName = "${contact.firstName ?: ""} ${contact.lastName ?: ""}".lowercase()
            val firstName = contact.firstName?.lowercase() ?: ""
            val lastName = contact.lastName?.lowercase() ?: ""
            
            fullName.contains(lowercaseQuery) ||
            firstName.contains(lowercaseQuery) ||
            lastName.contains(lowercaseQuery)
        }
    }
    
    fun cancelDebounceAndPrepareForSave() {
        searchDebounceJob?.cancel()
        searchDebounceJob = null
    }
    
    fun setDebounceJob(job: Job) {
        searchDebounceJob?.cancel()
        searchDebounceJob = job
    }
    
    fun saveToSearchHistory(query: String) {
        if (query.isNotBlank()) {
            searchHistoryManager.addSearchQuery(query.trim())
            loadSearchHistory()
        }
    }
    
    fun loadSearchHistory() {
        _searchHistory.value = searchHistoryManager.getSearchHistory()
    }
    
    fun removeFromHistory(query: String) {
        searchHistoryManager.removeSearchQuery(query)
        loadSearchHistory()
    }
    
    fun clearSearchHistory() {
        searchHistoryManager.clearHistory()
        loadSearchHistory()
    }
    
    fun cancelSearchDebounce() {
        searchDebounceJob?.cancel()
    }
}
