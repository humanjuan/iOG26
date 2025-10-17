package com.humanjuan.iog26.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Settings::class,
        BlockedNumber::class,
        BlockedPrefixRule::class,
        BlockedEvent::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun settings(): SettingsDao
    abstract fun numbers(): BlockedNumberDao
    abstract fun prefixes(): BlockedPrefixDao
    abstract fun events(): BlockedEventDao

    companion object {
        @Volatile private var INSTANCE: AppDb? = null
        fun get(ctx: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(ctx, AppDb::class.java, "app.db")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}