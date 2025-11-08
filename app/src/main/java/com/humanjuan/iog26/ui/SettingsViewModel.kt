package com.humanjuan.iog26.ui

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.humanjuan.iog26.data.AppDb
import com.humanjuan.iog26.data.DigestSettings
import com.humanjuan.iog26.data.DigestSettingsRepo
import com.humanjuan.iog26.data.Settings
import com.humanjuan.iog26.data.AppPrefsRepo
import com.humanjuan.iog26.ui.theme.Strings
import com.humanjuan.iog26.ui.theme.StringsEn
import com.humanjuan.iog26.ui.theme.StringsEs
import com.humanjuan.iog26.ui.theme.StringsIt
import com.humanjuan.iog26.digest.DailyDigestWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

data class SettingsUi(
    val blockAnonymousEnabled: Boolean = true,
    val blockUnknownContactsEnabled: Boolean = false,
    // UI uses legacy names for minimal changes; semantics: true => ENABLED (log and notify)
    val skipCallLogOnBlock: Boolean = true,
    val skipNotificationOnBlock: Boolean = true,
    val digestEnabled: Boolean = true,
    val digestHour: Int = 18,
    val digestMinute: Int = 0
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDb.get(app)
    private val digestRepo = DigestSettingsRepo(app)
    private val prefsRepo = AppPrefsRepo(app)

    // Para optimizaciones/lived state de Room
    private val _settingsRoom = MutableStateFlow(Settings())
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private suspend fun strings(): Strings {
        val lang = try {
            prefsRepo.flow.first().language
        } catch (_: Throwable) { "ES" }
        return when (lang.uppercase()) {
            "EN" -> StringsEn
            "IT" -> StringsIt
            else -> StringsEs
        }
    }

    val ui: StateFlow<SettingsUi> =
        combine(digestRepo.flow, _settingsRoom) { digest, room ->
            SettingsUi(
                blockAnonymousEnabled = room.blockAnonymousEnabled,
                blockUnknownContactsEnabled = room.blockUnknownContactsEnabled,
                // Map new positive-enable flags to UI state (legacy names)
                skipCallLogOnBlock = room.logBlockedCallsEnabled,
                skipNotificationOnBlock = room.notifyOnBlockEnabled,
                digestEnabled = digest.enabled,
                digestHour = digest.hour,
                digestMinute = digest.minute
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUi())

    init {
        viewModelScope.launch {
            // Semilla en Room si no existe
            val existing = db.settings().get()
            if (existing == null) {
                db.settings().upsert(Settings())
                _settingsRoom.value = Settings()
            } else {
                _settingsRoom.value = existing
            }
        }
    }

    fun setBlockAnonymous(enabled: Boolean) = viewModelScope.launch {
        val s = (_settingsRoom.value).copy(blockAnonymousEnabled = enabled)
        db.settings().upsert(s); _settingsRoom.value = s
        val str = strings()
        _events.tryEmit(if (enabled) str.snackAnonymousOn else str.snackAnonymousOff)
    }

    fun setBlockUnknownContacts(enabled: Boolean) = viewModelScope.launch {
        val s = (_settingsRoom.value).copy(blockUnknownContactsEnabled = enabled)
        db.settings().upsert(s); _settingsRoom.value = s
        val str = strings()
        _events.tryEmit(if (enabled) str.snackUnknownContactsOn else str.snackUnknownContactsOff)
    }

    fun setSkipCallLog(enabled: Boolean) = viewModelScope.launch {
        val s = (_settingsRoom.value).copy(logBlockedCallsEnabled = enabled)
        db.settings().upsert(s); _settingsRoom.value = s
        val str = strings()
        _events.tryEmit(if (enabled) str.snackLogOn else str.snackLogOff)
    }

    fun setSkipNotif(enabled: Boolean) = viewModelScope.launch {
        val s = (_settingsRoom.value).copy(notifyOnBlockEnabled = enabled)
        db.settings().upsert(s); _settingsRoom.value = s
        val str = strings()
        _events.tryEmit(if (enabled) str.snackNotifyOn else str.snackNotifyOff)
    }

    fun setDigestEnabled(enabled: Boolean) = viewModelScope.launch {
        digestRepo.setEnabled(enabled)
        val str = strings()
        if (enabled) {
            val curr = ui.value
            DailyDigestWorker.schedule(getApplication(), curr.digestHour, curr.digestMinute)
        }
        _events.tryEmit(if (enabled) str.snackDigestOn else str.snackDigestOff)
    }

    fun setDigestTime(hour: Int, minute: Int) = viewModelScope.launch {
        digestRepo.setTime(hour, minute)
        val enabled = ui.value.digestEnabled
        if (enabled) {
            DailyDigestWorker.schedule(getApplication(), hour, minute)
        }
        val str = strings()
        _events.tryEmit(str.snackDigestTimeSet.format(hour, minute))
    }
}