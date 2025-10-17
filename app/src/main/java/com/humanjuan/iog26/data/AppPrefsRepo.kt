package com.humanjuan.iog26.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appPrefsDataStore by preferencesDataStore("app_prefs.preferences_pb")

/**
 * UI preferences persisted via DataStore: theme and language.
 */
data class AppPrefs(
    val theme: String = "GREEN",
    val language: String = "ES"
)

object AppPrefsKeys {
    val THEME = stringPreferencesKey("ui_theme")
    val LANGUAGE = stringPreferencesKey("ui_language")
}

class AppPrefsRepo(private val context: Context) {
    val flow: Flow<AppPrefs> = context.appPrefsDataStore.data.map { p ->
        AppPrefs(
            theme = p[AppPrefsKeys.THEME] ?: "GREEN",
            language = p[AppPrefsKeys.LANGUAGE] ?: "ES"
        )
    }

    suspend fun setTheme(theme: String) {
        context.appPrefsDataStore.edit { it[AppPrefsKeys.THEME] = theme }
    }

    suspend fun setLanguage(lang: String) {
        context.appPrefsDataStore.edit { it[AppPrefsKeys.LANGUAGE] = lang }
    }
}
