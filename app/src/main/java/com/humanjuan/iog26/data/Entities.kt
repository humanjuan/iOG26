package com.humanjuan.iog26.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 0,
    val blockUnknownEnabled: Boolean = true,
    val skipCallLogOnBlock: Boolean = false,
    val skipNotificationOnBlock: Boolean = true
)

@Entity(tableName = "blocked_numbers")
data class BlockedNumber(
    @PrimaryKey val e164: String,
    val createdAt: Long
)

enum class PrefixScope { BY_COUNTRY, NATIONAL }

@Entity(tableName = "blocked_prefix_rules")
data class BlockedPrefixRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scope: PrefixScope,       // BY_COUNTRY: +CC + prefijo; NATIONAL: prefijo sobre NSN
    val countryCode: Int?,
    val prefixDigits: String,     // ej. "800", "600", "900"
    val createdAt: Long           // timestamp when rule was added/blocked
)

@Entity(tableName = "blocked_events")
data class BlockedEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val e164: String?,
    val ts: Long
)
