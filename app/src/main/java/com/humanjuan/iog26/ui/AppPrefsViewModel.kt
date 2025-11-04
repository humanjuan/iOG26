package com.humanjuan.iog26.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.humanjuan.iog26.data.AppPrefs
import com.humanjuan.iog26.data.AppPrefsRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppPrefsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AppPrefsRepo(app)

    private val initialPrefs: AppPrefs = try {
        // Synchronously load the first persisted value to avoid theme flash at startup
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            repo.flow.first()
        }
    } catch (_: Throwable) {
        AppPrefs()
    }

    val prefs: StateFlow<AppPrefs> = repo.flow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = initialPrefs
    )

    fun setTheme(theme: String) = viewModelScope.launch { repo.setTheme(theme) }
    fun setThemeMode(mode: String) = viewModelScope.launch { repo.setThemeMode(mode) }
    fun setLanguage(lang: String) = viewModelScope.launch { repo.setLanguage(lang) }
    fun setDevRegexMode(enabled: Boolean) = viewModelScope.launch { repo.setDevRegexMode(enabled) }
}
