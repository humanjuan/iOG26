package com.humanjuan.iog26.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.humanjuan.iog26.data.AppDb
import com.humanjuan.iog26.data.BlockedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Locale

// Ranking por número
 data class BlockedNumberGroup(
    val number: String,
    val count: Int,
    val mostRecentTimestamp: Long
)

// Inventario de reglas
 data class InventoryCounts(
    val blockedNumbers: Int = 0,
    val blockedPrefixes: Int = 0
)

// Desglose por tipo de llamante
 data class CallerBreakdown(
    val anonymous: Int = 0,        // sin caller ID (e164 null/blank)
    val unknownContacts: Int = 0,  // no está en contactos (con número)
    val known: Int = 0             // en contactos
)

// Estadística por país
 data class CountryStat(
    val regionCode: String,
    val countryName: String,
    val count: Int
)

class EventsViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDb.get(app)

    private val _rawItems = MutableStateFlow<List<BlockedEvent>>(emptyList())
    val rawItems: StateFlow<List<BlockedEvent>> = _rawItems

    private val _groupedItems = MutableStateFlow<List<BlockedNumberGroup>>(emptyList())
    val groupedItems: StateFlow<List<BlockedNumberGroup>> = _groupedItems

    private val _inventory = MutableStateFlow(InventoryCounts())
    val inventory: StateFlow<InventoryCounts> = _inventory

    private val _callerBreakdown = MutableStateFlow(CallerBreakdown())
    val callerBreakdown: StateFlow<CallerBreakdown> = _callerBreakdown

    private val _countryStats = MutableStateFlow<List<CountryStat>>(emptyList())
    val countryStats: StateFlow<List<CountryStat>> = _countryStats

    fun load(daysBack: Long = 0) = viewModelScope.launch(Dispatchers.IO) {
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

        // DB reads
        val events = db.events().since(sinceMillis)
        val numbers = db.numbers().all()
        val prefixes = db.prefixes().all()

        // Publish raw and grouped
        _rawItems.value = events
        _groupedItems.value = events
            .groupBy { it.e164 ?: "Unknown" }
            .map {
                val mostRecent = it.value.maxByOrNull { event -> event.ts }!!
                BlockedNumberGroup(number = it.key, count = it.value.size, mostRecentTimestamp = mostRecent.ts)
            }
            .sortedByDescending { it.mostRecentTimestamp }

        // Inventory
        _inventory.value = InventoryCounts(blockedNumbers = numbers.size, blockedPrefixes = prefixes.size)

        // Caller breakdown (robust contact membership with permission check)
        val ctx = getApplication<Application>()
        val resolver = ctx.contentResolver
        val hasPerm = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        var anonymous = 0
        var unknown = 0
        var known = 0
        // Preload contact numbers once if permission is granted for fallback matching
        val allContacts: List<String> = if (hasPerm) loadAllContactNumbers(resolver) else emptyList()

        events.forEach { e ->
            val n = e.e164
            if (n.isNullOrBlank()) {
                anonymous++
            } else {
                val inContacts = if (hasPerm) isInContacts(resolver, n, allContacts) else false
                if (inContacts) known++ else unknown++
            }
        }
        _callerBreakdown.value = CallerBreakdown(anonymous = anonymous, unknownContacts = unknown, known = known)

        // Country stats
        val util = PhoneNumberUtil.getInstance()
        val locale = Locale.getDefault()
        val counts = mutableMapOf<String, Int>()
        events.forEach { e ->
            val n = e.e164
            if (!n.isNullOrBlank()) {
                runCatching {
                    val p = util.parse(n, null)
                    val region = util.getRegionCodeForNumber(p)
                    if (!region.isNullOrBlank()) counts[region] = (counts[region] ?: 0) + 1
                }
            }
        }
        _countryStats.value = counts.entries
            .map { (region, c) ->
                val name = Locale("", region).getDisplayCountry(locale).ifBlank { region }
                CountryStat(regionCode = region, countryName = name, count = c)
            }
            .sortedByDescending { it.count }
        // end load
    }

    // Load only the recent grouped list for the given range; keep indicators/charts intact
    fun loadRecentFor(daysBack: Long) = viewModelScope.launch(Dispatchers.IO) {
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
        _groupedItems.value = events
            .groupBy { it.e164 ?: "Unknown" }
            .map {
                val mostRecent = it.value.maxByOrNull { event -> event.ts }!!
                BlockedNumberGroup(number = it.key, count = it.value.size, mostRecentTimestamp = mostRecent.ts)
            }
            .sortedByDescending { it.mostRecentTimestamp }
    }

    // --- Contacts helpers ---
    private fun loadAllContactNumbers(resolver: android.content.ContentResolver): List<String> {
        return try {
            val numbers = mutableListOf<String>()
            resolver.query(
                android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                null
            )?.use { c ->
                val idx = 0
                while (c.moveToNext()) {
                    val num = c.getString(idx)
                    if (!num.isNullOrBlank()) numbers += num
                }
            }
            numbers
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun isInContacts(
        resolver: android.content.ContentResolver,
        number: String,
        allContacts: List<String>
    ): Boolean {
        // Quick: PhoneLookup by original number
        try {
            val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
                .appendPath(android.net.Uri.encode(number))
                .build()
            resolver.query(
                uri,
                arrayOf(ContactsContract.PhoneLookup._ID),
                null,
                null,
                null
            )?.use { c -> if (c.moveToFirst()) return true }
        } catch (_: Throwable) { }

        // Fallback: compare against cached list using PhoneNumberUtils.compare
        for (stored in allContacts) {
            try {
                if (PhoneNumberUtils.compare(stored, number)) return true
            } catch (_: Throwable) { }
        }
        return false
    }
}
