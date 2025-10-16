package com.example.nexoftcontacts.utils

import android.content.Context
import android.content.SharedPreferences

class SearchHistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "search_history_prefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_SEARCH_HISTORY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
        private const val DELIMITER = "|||"
    }
    
    fun addSearchQuery(query: String) {
        if (query.isBlank()) return
        
        val currentHistory = getSearchHistory().toMutableList()
        
        // Remove if already exists (to move to top)
        currentHistory.remove(query)
        
        // Add to beginning
        currentHistory.add(0, query)
        
        // Keep only last MAX_HISTORY_SIZE items
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.subList(MAX_HISTORY_SIZE, currentHistory.size).clear()
        }
        
        saveHistory(currentHistory)
    }
    
    fun getSearchHistory(): List<String> {
        val historyString = prefs.getString(KEY_SEARCH_HISTORY, "") ?: ""
        return if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split(DELIMITER).filter { it.isNotBlank() }
        }
    }
    
    fun removeSearchQuery(query: String) {
        val currentHistory = getSearchHistory().toMutableList()
        currentHistory.remove(query)
        saveHistory(currentHistory)
    }
    
    fun clearHistory() {
        prefs.edit().remove(KEY_SEARCH_HISTORY).apply()
    }
    
    private fun saveHistory(history: List<String>) {
        val historyString = history.joinToString(DELIMITER)
        prefs.edit().putString(KEY_SEARCH_HISTORY, historyString).apply()
    }
}
