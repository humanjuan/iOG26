package com.humanjuan.iog26.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appPrefsDataStore by preferencesDataStore("app_prefs.preferences_pb")

/**
 * UI preferences persisted via DataStore: theme palette, theme mode and language.
 */
data class AppPrefs(
    val theme: String = "IOG26",
    val themeMode: String = "SYSTEM", // SYSTEM | LIGHT | DARK
    val language: String = "ES",
    val devRegexMode: Boolean = false
)

object AppPrefsKeys {
    val THEME = stringPreferencesKey("ui_theme")
    val THEME_MODE = stringPreferencesKey("ui_theme_mode")
    val LANGUAGE = stringPreferencesKey("ui_language")
    val DEV_REGEX_MODE = booleanPreferencesKey("dev_regex_mode")
}

class AppPrefsRepo(private val context: Context) {
    val flow: Flow<AppPrefs> = context.appPrefsDataStore.data.map { p ->
        AppPrefs(
            theme = p[AppPrefsKeys.THEME] ?: "IOG26",
            themeMode = p[AppPrefsKeys.THEME_MODE] ?: "SYSTEM",
            language = p[AppPrefsKeys.LANGUAGE] ?: "ES",
            devRegexMode = p[AppPrefsKeys.DEV_REGEX_MODE] ?: false
        )
    }

    suspend fun setTheme(theme: String) {
        context.appPrefsDataStore.edit { it[AppPrefsKeys.THEME] = theme }
    }

    suspend fun setThemeMode(mode: String) {
        context.appPrefsDataStore.edit { it[AppPrefsKeys.THEME_MODE] = mode }
    }

    suspend fun setLanguage(lang: String) {
        context.appPrefsDataStore.edit { it[AppPrefsKeys.LANGUAGE] = lang }
    }

    suspend fun setDevRegexMode(enabled: Boolean) {
        context.appPrefsDataStore.edit { it[AppPrefsKeys.DEV_REGEX_MODE] = enabled }
    }
}
