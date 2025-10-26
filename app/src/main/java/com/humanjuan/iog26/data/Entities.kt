package com.humanjuan.iog26.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 0,
    // Separate controls:
    // - blockAnonymousEnabled: block callers without caller ID (restricted/unknown/private). Enabled by default.
    // - blockUnknownContactsEnabled: block numbers not found in contacts. Disabled by default.
    // - logBlockedCallsEnabled: create a phone call log entry for blocked calls. Enabled by default.
    // - notifyOnBlockEnabled: show a notification when a call is blocked. Enabled by default.
    val blockAnonymousEnabled: Boolean = true,
    val blockUnknownContactsEnabled: Boolean = false,
    val logBlockedCallsEnabled: Boolean = true,
    val notifyOnBlockEnabled: Boolean = true
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
