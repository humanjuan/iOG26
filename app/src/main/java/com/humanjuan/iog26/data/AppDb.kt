package com.humanjuan.iog26.data

import android.content.Context
import android.database.Cursor
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureSchema(db)
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureSchema(db)
            }
        }

        fun get(ctx: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(ctx, AppDb::class.java, "app.db")
                    // Replace destructive fallback with explicit migrations to keep data
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }

        private fun ensureSchema(db: SupportSQLiteDatabase) {
            // settings
            createTableIfNotExists(db, "settings", "CREATE TABLE IF NOT EXISTS settings (id INTEGER NOT NULL PRIMARY KEY, blockAnonymousEnabled INTEGER NOT NULL DEFAULT 1, blockUnknownContactsEnabled INTEGER NOT NULL DEFAULT 0, logBlockedCallsEnabled INTEGER NOT NULL DEFAULT 1, notifyOnBlockEnabled INTEGER NOT NULL DEFAULT 1)")
            addColumnIfMissing(db, "settings", "blockAnonymousEnabled", "INTEGER NOT NULL DEFAULT 1")
            addColumnIfMissing(db, "settings", "blockUnknownContactsEnabled", "INTEGER NOT NULL DEFAULT 0")
            addColumnIfMissing(db, "settings", "logBlockedCallsEnabled", "INTEGER NOT NULL DEFAULT 1")
            addColumnIfMissing(db, "settings", "notifyOnBlockEnabled", "INTEGER NOT NULL DEFAULT 1")
            addColumnIfMissing(db, "settings", "id", "INTEGER NOT NULL DEFAULT 0")

            // blocked_numbers
            createTableIfNotExists(db, "blocked_numbers", "CREATE TABLE IF NOT EXISTS blocked_numbers (e164 TEXT NOT NULL PRIMARY KEY, createdAt INTEGER NOT NULL DEFAULT 0)")
            addColumnIfMissing(db, "blocked_numbers", "createdAt", "INTEGER NOT NULL DEFAULT 0")

            // blocked_prefix_rules
            createTableIfNotExists(db, "blocked_prefix_rules", "CREATE TABLE IF NOT EXISTS blocked_prefix_rules (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, scope TEXT NOT NULL DEFAULT 'BY_COUNTRY', countryCode INTEGER, prefixDigits TEXT NOT NULL DEFAULT '', createdAt INTEGER NOT NULL DEFAULT 0)")
            addColumnIfMissing(db, "blocked_prefix_rules", "scope", "TEXT NOT NULL DEFAULT 'BY_COUNTRY'")
            addColumnIfMissing(db, "blocked_prefix_rules", "countryCode", "INTEGER")
            addColumnIfMissing(db, "blocked_prefix_rules", "prefixDigits", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, "blocked_prefix_rules", "createdAt", "INTEGER NOT NULL DEFAULT 0")

            // blocked_events
            createTableIfNotExists(db, "blocked_events", "CREATE TABLE IF NOT EXISTS blocked_events (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, e164 TEXT, ts INTEGER NOT NULL DEFAULT 0)")
            addColumnIfMissing(db, "blocked_events", "e164", "TEXT")
            addColumnIfMissing(db, "blocked_events", "ts", "INTEGER NOT NULL DEFAULT 0")
        }

        private fun createTableIfNotExists(db: SupportSQLiteDatabase, table: String, createSql: String) {
            if (!tableExists(db, table)) {
                db.execSQL(createSql)
            }
        }

        private fun addColumnIfMissing(db: SupportSQLiteDatabase, table: String, column: String, definition: String) {
            if (!columnExists(db, table, column)) {
                db.execSQL("ALTER TABLE $table ADD COLUMN $column $definition")
            }
        }

        private fun tableExists(db: SupportSQLiteDatabase, table: String): Boolean {
            val c: Cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(table))
            c.use { return it.moveToFirst() }
        }

        private fun columnExists(db: SupportSQLiteDatabase, table: String, column: String): Boolean {
            val cursor: Cursor = db.query("PRAGMA table_info($table)")
            cursor.use {
                val nameIndex = it.getColumnIndex("name")
                while (it.moveToNext()) {
                    if (nameIndex >= 0 && it.getString(nameIndex) == column) return true
                }
            }
            return false
        }
    }
}