package com.humanjuan.iog26.data

import androidx.room.*

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id=0")
    suspend fun get(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(s: Settings)
}

@Dao
interface BlockedNumberDao {
    @Query("SELECT * FROM blocked_numbers")
    suspend fun all(): List<BlockedNumber>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: BlockedNumber)

    @Query("DELETE FROM blocked_numbers WHERE e164 = :e164")
    suspend fun remove(e164: String)
}

@Dao
interface BlockedPrefixDao {
    @Query("SELECT * FROM blocked_prefix_rules")
    suspend fun all(): List<BlockedPrefixRule>

    @Insert
    suspend fun add(item: BlockedPrefixRule)

    @Query("DELETE FROM blocked_prefix_rules WHERE id = :id")
    suspend fun remove(id: Long)
}

@Dao
interface BlockedEventDao {
    @Insert
    suspend fun add(e: BlockedEvent)

    @Query("SELECT * FROM blocked_events WHERE ts >= :since ORDER BY ts DESC")
    suspend fun since(since: Long): List<BlockedEvent>
}
