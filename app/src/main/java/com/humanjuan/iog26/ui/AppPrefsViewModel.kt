package com.humanjuan.iog26.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.humanjuan.iog26.data.AppPrefs
import com.humanjuan.iog26.data.AppPrefsRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppPrefsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AppPrefsRepo(app)

    val prefs: StateFlow<AppPrefs> = repo.flow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppPrefs()
    )

    fun setTheme(theme: String) = viewModelScope.launch { repo.setTheme(theme) }
    fun setLanguage(lang: String) = viewModelScope.launch { repo.setLanguage(lang) }
}
