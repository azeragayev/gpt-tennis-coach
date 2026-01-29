package com.aaa.gptenniscoach.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aaa.gptenniscoach.data.db.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for session database operations.
 */
@Dao
interface SessionDao {
    /**
     * Observes all sessions ordered by creation date.
     */
    @Query("SELECT * FROM sessions ORDER BY createdAtMs DESC")
    fun observeAll(): Flow<List<SessionEntity>>

    /**
     * Observes a specific session by ID.
     */
    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<SessionEntity?>

    /**
     * Inserts or replaces a session entity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SessionEntity)

    /**
     * Retrieves a single session by ID synchronously.
     */
    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    suspend fun getByIdOnce(id: String): SessionEntity?

    /**
     * Deletes a session by ID.
     */
    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Retrieves all sessions synchronously.
     */
    @Query("SELECT * FROM sessions")
    suspend fun getAllOnce(): List<SessionEntity>

    /**
     * Deletes all sessions from the database.
     */
    @Query("DELETE FROM sessions")
    suspend fun deleteAll()

}

