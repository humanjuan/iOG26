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
        BlockedEvent::class,
        RegexRule::class
    ],
    version = 5,
    exportSchema = true
)
abstract class AppDb : RoomDatabase() {
    abstract fun settings(): SettingsDao
    abstract fun numbers(): BlockedNumberDao
    abstract fun prefixes(): BlockedPrefixDao
    abstract fun events(): BlockedEventDao
    abstract fun regex(): RegexRuleDao

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
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureSchema(db)
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureSchema(db)
            }
        }

        fun get(ctx: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(ctx, AppDb::class.java, "app.db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                    .also { INSTANCE = it }
            }

        private fun ensureSchema(db: SupportSQLiteDatabase) {
            fixSettingsTable(db)

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

            // regex_rules
            createTableIfNotExists(db, "regex_rules", "CREATE TABLE IF NOT EXISTS regex_rules (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, kind TEXT NOT NULL, pattern TEXT NOT NULL, createdAt INTEGER NOT NULL DEFAULT 0)")
            addColumnIfMissing(db, "regex_rules", "kind", "TEXT NOT NULL")
            addColumnIfMissing(db, "regex_rules", "pattern", "TEXT NOT NULL")
            addColumnIfMissing(db, "regex_rules", "createdAt", "INTEGER NOT NULL DEFAULT 0")
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

        private fun isPrimaryKey(db: SupportSQLiteDatabase, table: String, column: String): Boolean {
            val cursor: Cursor = db.query("PRAGMA table_info($table)")
            cursor.use {
                val nameIndex = it.getColumnIndex("name")
                val pkIndex = it.getColumnIndex("pk")
                while (it.moveToNext()) {
                    if (nameIndex >= 0 && pkIndex >= 0) {
                        val name = it.getString(nameIndex)
                        if (name == column) {
                            val pkFlag = it.getInt(pkIndex)
                            return pkFlag > 0
                        }
                    }
                }
            }
            return false
        }

        private fun fixSettingsTable(db: SupportSQLiteDatabase) {
            val table = "settings"
            val createSql = "CREATE TABLE IF NOT EXISTS settings (id INTEGER NOT NULL PRIMARY KEY, blockAnonymousEnabled INTEGER NOT NULL DEFAULT 1, blockUnknownContactsEnabled INTEGER NOT NULL DEFAULT 0, logBlockedCallsEnabled INTEGER NOT NULL DEFAULT 1, notifyOnBlockEnabled INTEGER NOT NULL DEFAULT 1)"

            if (!tableExists(db, table)) {
                // No existe: crear con el esquema final y preparar fila por defecto
                db.execSQL(createSql)
                // Asegurar una fila por defecto id=0 (INSERT OR IGNORE por id PK)
                db.execSQL("INSERT OR IGNORE INTO settings (id) VALUES (0)")
                return
            }

            // Existe: verificar que 'id' sea PK real
            val idIsPk = isPrimaryKey(db, table, "id")
            if (!idIsPk) {
                // Recrear de forma atómica
                db.beginTransaction()
                try {
                    db.execSQL("""
                        CREATE TABLE settings_new (
                          id INTEGER NOT NULL PRIMARY KEY,
                          blockAnonymousEnabled INTEGER NOT NULL DEFAULT 1,
                          blockUnknownContactsEnabled INTEGER NOT NULL DEFAULT 0,
                          logBlockedCallsEnabled INTEGER NOT NULL DEFAULT 1,
                          notifyOnBlockEnabled INTEGER NOT NULL DEFAULT 1
                        )
                    """.trimIndent())

                    val hasAnon = columnExists(db, table, "blockAnonymousEnabled")
                    val hasUnknown = columnExists(db, table, "blockUnknownContactsEnabled")
                    val hasLog = columnExists(db, table, "logBlockedCallsEnabled")
                    val hasNotify = columnExists(db, table, "notifyOnBlockEnabled")

                    val exprAnon = if (hasAnon) "COALESCE(blockAnonymousEnabled, 1)" else "1"
                    val exprUnknown = if (hasUnknown) "COALESCE(blockUnknownContactsEnabled, 0)" else "0"
                    val exprLog = if (hasLog) "COALESCE(logBlockedCallsEnabled, 1)" else "1"
                    val exprNotify = if (hasNotify) "COALESCE(notifyOnBlockEnabled, 1)" else "1"

                    if (tableHasAnyRow(db, table)) {
                        val insertSql = """
                            INSERT INTO settings_new (
                              id, blockAnonymousEnabled, blockUnknownContactsEnabled, logBlockedCallsEnabled, notifyOnBlockEnabled
                            )
                            SELECT 0, $exprAnon, $exprUnknown, $exprLog, $exprNotify FROM $table
                        """.trimIndent()
                        db.execSQL(insertSql)
                    }

                    db.execSQL("DROP TABLE IF EXISTS $table")
                    db.execSQL("ALTER TABLE settings_new RENAME TO $table")

                    db.execSQL("INSERT OR IGNORE INTO settings (id) VALUES (0)")

                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            } else {
                // Ya tiene PK correcta: asegurar columnas y fila por defecto
                addColumnIfMissing(db, table, "blockAnonymousEnabled", "INTEGER NOT NULL DEFAULT 1")
                addColumnIfMissing(db, table, "blockUnknownContactsEnabled", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfMissing(db, table, "logBlockedCallsEnabled", "INTEGER NOT NULL DEFAULT 1")
                addColumnIfMissing(db, table, "notifyOnBlockEnabled", "INTEGER NOT NULL DEFAULT 1")
                // Insertar fila por defecto si no existe
                db.execSQL("INSERT OR IGNORE INTO settings (id) VALUES (0)")
            }
        }

        private fun tableHasAnyRow(db: SupportSQLiteDatabase, table: String): Boolean {
            val c = db.query("SELECT 1 FROM $table LIMIT 1")
            c.use { return it.moveToFirst() }
        }
    }
}