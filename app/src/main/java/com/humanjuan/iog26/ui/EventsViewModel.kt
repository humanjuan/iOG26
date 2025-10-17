package com.humanjuan.iog26.ui

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.humanjuan.iog26.data.AppDb
import com.humanjuan.iog26.data.BlockedEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.Calendar

data class BlockedNumberGroup(
    val number: String,
    val count: Int,
    val mostRecentTimestamp: Long
)

class EventsViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDb.get(app)

    private val _rawItems = MutableStateFlow<List<BlockedEvent>>(emptyList())
    val rawItems: StateFlow<List<BlockedEvent>> = _rawItems

    private val _groupedItems = MutableStateFlow<List<BlockedNumberGroup>>(emptyList())
    val groupedItems: StateFlow<List<BlockedNumberGroup>> = _groupedItems

    fun load(daysBack: Long = 0) = viewModelScope.launch {
        val sinceMillis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val now = ZonedDateTime.now()
            val start = now.minusDays(daysBack).withHour(0).withMinute(0).withSecond(0).withNano(0)
            start.toInstant().toEpochMilli()
        } else {
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysBack.toInt())
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        }
        val events = db.events().since(sinceMillis)
        _rawItems.value = events
        _groupedItems.value = events
            .groupBy { it.e164 ?: "Unknown" }
            .map {
                val mostRecent = it.value.maxByOrNull { event -> event.ts }!!
                BlockedNumberGroup(number = it.key, count = it.value.size, mostRecentTimestamp = mostRecent.ts)
            }
            .sortedByDescending { it.mostRecentTimestamp }
    }
}
