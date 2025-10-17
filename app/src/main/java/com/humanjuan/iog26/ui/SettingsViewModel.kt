package com.humanjuan.iog26.ui

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.humanjuan.iog26.data.AppDb
import com.humanjuan.iog26.data.DigestSettings
import com.humanjuan.iog26.data.DigestSettingsRepo
import com.humanjuan.iog26.data.Settings
import com.humanjuan.iog26.digest.DailyDigestWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class SettingsUi(
    val blockUnknownEnabled: Boolean = true,
    val skipCallLogOnBlock: Boolean = false,
    val skipNotificationOnBlock: Boolean = true,
    val digestEnabled: Boolean = true,
    val digestHour: Int = 18,
    val digestMinute: Int = 0
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDb.get(app)
    private val digestRepo = DigestSettingsRepo(app)

    // Para optimizaciones/lived state de Room
    private val _settingsRoom = MutableStateFlow(Settings())
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    val ui: StateFlow<SettingsUi> =
        combine(digestRepo.flow, _settingsRoom) { digest, room ->
            SettingsUi(
                blockUnknownEnabled = room.blockUnknownEnabled,
                skipCallLogOnBlock = room.skipCallLogOnBlock,
                skipNotificationOnBlock = room.skipNotificationOnBlock,
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

    fun setBlockUnknown(enabled: Boolean) = viewModelScope.launch {
        val s = (_settingsRoom.value).copy(blockUnknownEnabled = enabled)
        db.settings().upsert(s); _settingsRoom.value = s
        _events.tryEmit(if (enabled) "Bloqueo de desconocidos ACTIVADO" else "Bloqueo de desconocidos DESACTIVADO")
    }

    fun setSkipCallLog(enabled: Boolean) = viewModelScope.launch {
        val s = (_settingsRoom.value).copy(skipCallLogOnBlock = enabled)
        db.settings().upsert(s); _settingsRoom.value = s
        _events.tryEmit(if (enabled) "No registrar en historial: ACTIVADO" else "No registrar en historial: DESACTIVADO")
    }

    fun setSkipNotif(enabled: Boolean) = viewModelScope.launch {
        val s = (_settingsRoom.value).copy(skipNotificationOnBlock = enabled)
        db.settings().upsert(s); _settingsRoom.value = s
        _events.tryEmit(if (enabled) "Silenciar notificación: ACTIVADO" else "Silenciar notificación: DESACTIVADO")
    }

    fun setDigestEnabled(enabled: Boolean) = viewModelScope.launch {
        digestRepo.setEnabled(enabled)
        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val curr = ui.value
            DailyDigestWorker.schedule(getApplication(), curr.digestHour, curr.digestMinute)
        }
        if (enabled && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            _events.tryEmit("Resumen diario requiere Android 8.0+ (no se puede programar en este dispositivo)")
        } else {
            _events.tryEmit(if (enabled) "Resumen diario ACTIVADO" else "Resumen diario DESACTIVADO")
        }
    }

    fun setDigestTime(hour: Int, minute: Int) = viewModelScope.launch {
        digestRepo.setTime(hour, minute)
        val enabled = ui.value.digestEnabled
        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DailyDigestWorker.schedule(getApplication(), hour, minute)
        }
        _events.tryEmit("Hora del resumen: %02d:%02d".format(hour, minute))
    }
}