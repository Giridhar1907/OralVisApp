package com.example.oralvisapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oralvisapp.data.AppDatabase
import com.example.oralvisapp.data.Session
import com.example.oralvisapp.data.SessionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SessionRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SessionRepository(database.sessionDao(), application)
    }

    // Current session state
    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _capturedImages = MutableStateFlow<List<File>>(emptyList())
    val capturedImages: StateFlow<List<File>> = _capturedImages.asStateFlow()

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    // Search functionality
    private val _searchResults = MutableStateFlow<Session?>(null)
    val searchResults: StateFlow<Session?> = _searchResults.asStateFlow()

    private val _searchImages = MutableStateFlow<List<File>>(emptyList())
    val searchImages: StateFlow<List<File>> = _searchImages.asStateFlow()

    fun startSession() {
        viewModelScope.launch {
            val sessionId = repository.generateSessionId()
            _currentSessionId.value = sessionId
            _isSessionActive.value = true
            _capturedImages.value = emptyList()
        }
    }

    fun addCapturedImage(imageFile: File) {
        _capturedImages.value = _capturedImages.value + imageFile
    }

    fun endSession(name: String, age: Int) {
        viewModelScope.launch {
            val sessionId = _currentSessionId.value
            if (sessionId != null) {
                val session = Session(
                    sessionId = sessionId,
                    name = name,
                    age = age,
                    timestamp = System.currentTimeMillis(),
                    imageCount = _capturedImages.value.size
                )
                repository.insertSession(session)

                _currentSessionId.value = null
                _isSessionActive.value = false
                _capturedImages.value = emptyList()
            }
        }
    }

    fun searchSession(sessionId: String) {
        viewModelScope.launch {
            val session = repository.getSessionById(sessionId)
            _searchResults.value = session

            if (session != null) {
                val images = repository.getSessionImages(sessionId)
                _searchImages.value = images
            } else {
                _searchImages.value = emptyList()
            }
        }
    }

    fun createImageFile(): File? {
        val sessionId = _currentSessionId.value
        return if (sessionId != null) {
            repository.createImageFile(sessionId)
        } else null
    }

    fun clearSearch() {
        _searchResults.value = null
        _searchImages.value = emptyList()
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            repository.deleteSession(session)
            // Clear search results if the deleted session was being viewed
            if (_searchResults.value?.sessionId == session.sessionId) {
                clearSearch()
            }
        }
    }
}