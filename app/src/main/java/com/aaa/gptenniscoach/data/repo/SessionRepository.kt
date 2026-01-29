package com.aaa.gptenniscoach.data.repo

import android.net.Uri
import kotlinx.coroutines.flow.Flow

data class PlayerMeta(
    val level: String,
    val handedness: String,
    val goal: String,
    val language: String,
    val detailMode: String
)

sealed class AnalyzeProgress {
    data object Copying : AnalyzeProgress()
    data class Uploading(val fraction: Float) : AnalyzeProgress()
    data object Analyzing : AnalyzeProgress()
    data class Success(val sessionId: String) : AnalyzeProgress()
    data class Error(val message: String, val retryable: Boolean) : AnalyzeProgress()
}

/**
 * Repository interface for managing tennis analysis sessions.
 */
interface SessionRepository {
    /**
     * Analyzes a video from a content URI and emits progress updates.
     */
    fun analyzeFromContentUri(uri: Uri, playerMeta: PlayerMeta): Flow<AnalyzeProgress>
    /**
     * Observes all stored analysis sessions.
     */
    fun observeSessions(): Flow<List<SessionSummary>>
    /**
     * Observes a specific analysis session by ID.
     */
    fun observeSession(id: String): Flow<SessionDetail?>
    /**
     * Deletes a specific session by ID.
     */
    suspend fun deleteSession(id: String)
    /**
     * Deletes all stored sessions.
     */
    suspend fun deleteAllSessions()   // ✅ ADD
}

data class SessionSummary(
    val id: String,
    val createdAtMs: Long,
    val quality: String?,
    val warningsCount: Int
)

data class SessionDetail(
    val id: String,
    val createdAtMs: Long,
    val videoLocalPath: String?,
    val videoContentUri: String?,
    val videoStorageMode: String,
    val playerMetaJson: String,
    val responseJson: String
)

