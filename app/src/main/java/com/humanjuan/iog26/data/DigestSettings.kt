package com.humanjuan.iog26.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.digestDataStore by preferencesDataStore("digest.preferences_pb")

data class DigestSettings(
    val enabled: Boolean = true,
    val hour: Int = 18,
    val minute: Int = 0
)

object DigestKeys {
    val ENABLED = booleanPreferencesKey("digest_enabled")
    val HOUR = intPreferencesKey("digest_hour")
    val MINUTE = intPreferencesKey("digest_minute")
}

class DigestSettingsRepo(private val context: Context) {
    val flow: Flow<DigestSettings> = context.digestDataStore.data.map {
        DigestSettings(
            it[DigestKeys.ENABLED] ?: true,
            it[DigestKeys.HOUR] ?: 18,
            it[DigestKeys.MINUTE] ?: 0
        )
    }

    suspend fun setEnabled(v: Boolean) = context.digestDataStore.edit { it[DigestKeys.ENABLED] = v }
    suspend fun setTime(h: Int, m: Int) = context.digestDataStore.edit {
        it[DigestKeys.HOUR] = h
        it[DigestKeys.MINUTE] = m
    }
}
