package com.aaa.gptenniscoach.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aaa.gptenniscoach.data.db.dao.SessionDao
import com.aaa.gptenniscoach.data.db.entity.SessionEntity

/**
 * Room database for storing analysis sessions and metadata.
 */
@Database(
    entities = [SessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Returns the DAO for accessing session database operations.
     */
    abstract fun sessionDao(): SessionDao

    companion object {
        /**
         * Creates and configures a Room database instance.
         */
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "tenniscoach.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}

