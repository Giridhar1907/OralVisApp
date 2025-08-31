package com.example.oralvisapp.data

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SessionRepository(private val sessionDao: SessionDao, private val context: Context) {

    fun getAllSessions(): Flow<List<Session>> = sessionDao.getAllSessions()

    suspend fun getSessionById(sessionId: String): Session? = sessionDao.getSessionById(sessionId)

    suspend fun insertSession(session: Session) = sessionDao.insertSession(session)

    suspend fun updateSession(session: Session) = sessionDao.updateSession(session)

    suspend fun updateImageCount(sessionId: String, imageCount: Int) =
        sessionDao.updateImageCount(sessionId, imageCount)

    fun getSessionDirectory(sessionId: String): File {
        val mediaDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Sessions/$sessionId"
        )
        if (!mediaDir.exists()) {
            mediaDir.mkdirs()
        }
        return mediaDir
    }

    fun createImageFile(sessionId: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_$timeStamp.jpg"
        return File(getSessionDirectory(sessionId), imageFileName)
    }

    fun getSessionImages(sessionId: String): List<File> {
        val sessionDir = getSessionDirectory(sessionId)
        return sessionDir.listFiles { file -> file.extension == "jpg" }?.toList() ?: emptyList()
    }

    suspend fun generateSessionId(): String {
        val sessionCount = sessionDao.getSessionCount()
        return "S${sessionCount + 1}"
    }

    suspend fun deleteSession(session: Session) {
        // Delete from database
        sessionDao.deleteSession(session)

        // Delete images from file system
        val sessionDir = getSessionDirectory(session.sessionId)
        if (sessionDir.exists()) {
            sessionDir.deleteRecursively()
        }
    }
}