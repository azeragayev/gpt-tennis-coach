package com.aaa.gptenniscoach.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity representing a stored analysis session.
 */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val createdAtMs: Long,
    val videoLocalPath: String?,
    val videoContentUri: String?,
    val videoStorageMode: String, // APP_COPY | URI_REF
    val playerMetaJson: String,
    val requestJson: String,
    val responseJson: String,
    val qualitySummary: String?,
    val warningsCount: Int
)

