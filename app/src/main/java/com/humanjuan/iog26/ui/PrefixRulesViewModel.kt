package com.humanjuan.iog26.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.humanjuan.iog26.data.AppDb
import com.humanjuan.iog26.data.BlockedPrefixRule
import com.humanjuan.iog26.data.PrefixScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiPrefixRule(
    val id: Long,
    val label: String,
    val scope: PrefixScope,
    val countryCode: Int?,
    val prefixDigits: String,
    val createdAt: Long
)

class PrefixRulesViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDb.get(app)
    private val _items = MutableStateFlow<List<UiPrefixRule>>(emptyList())
    val items: StateFlow<List<UiPrefixRule>> = _items

    data class UiRegexRule(val id: Long, val pattern: String, val createdAt: Long)
    private val _regexItems = MutableStateFlow<List<UiRegexRule>>(emptyList())
    val regexItems: StateFlow<List<UiRegexRule>> = _regexItems

    init { refresh(); refreshRegex() }

    fun refresh() = viewModelScope.launch {
        val list = db.prefixes().all().map {
            val label = when (it.scope) {
                PrefixScope.BY_COUNTRY -> "+${it.countryCode} ${it.prefixDigits}*"
                PrefixScope.NATIONAL   -> "${it.prefixDigits}* (NSN)"
            }
            UiPrefixRule(it.id, label, it.scope, it.countryCode, it.prefixDigits, it.createdAt)
        }.sortedBy { it.label }
        _items.value = list
    }

    fun refreshRegex() = viewModelScope.launch {
        _regexItems.value = db.regex().allByKind("PREFIX").map { UiRegexRule(it.id, it.pattern, it.createdAt) }
    }

    fun add(prefixDigitsRaw: String, countryCodeInput: String?): String? {
        val digits = prefixDigitsRaw.filter(Char::isDigit)
        if (digits.isEmpty()) return "Prefijo vacío o inválido"

        val countryCode = countryCodeInput?.trim().orEmpty().removePrefix("+").let {
            if (it.isBlank()) null else it.toIntOrNull()
        } ?: run {
            // país en blanco → NATIONAL
            viewModelScope.launch {
                db.prefixes().add(
                    BlockedPrefixRule(
                        scope = PrefixScope.NATIONAL,
                        countryCode = null,
                        prefixDigits = digits,
                        createdAt = System.currentTimeMillis()
                    )
                )
                refresh()
            }
            return null
        }

        viewModelScope.launch {
            db.prefixes().add(
                BlockedPrefixRule(
                    scope = PrefixScope.BY_COUNTRY,
                    countryCode = countryCode,
                    prefixDigits = digits,
                    createdAt = System.currentTimeMillis()
                )
            )
            refresh()
        }
        return null
    }

    fun addRegex(pattern: String): String? {
        return try {
            Regex(pattern)
            viewModelScope.launch {
                db.regex().add(com.humanjuan.iog26.data.RegexRule(
                    kind = "PREFIX",
                    pattern = pattern,
                    createdAt = System.currentTimeMillis()
                ))
                refreshRegex()
            }
            null
        } catch (t: Throwable) {
            t.message ?: com.humanjuan.iog26.ui.theme.StringsEs.regexInvalidMessage
        }
    }

    fun updateRegex(id: Long, pattern: String): String? {
        return try {
            Regex(pattern)
            viewModelScope.launch {
                db.regex().updatePattern(id, pattern)
                refreshRegex()
            }
            null
        } catch (t: Throwable) {
            t.message ?: com.humanjuan.iog26.ui.theme.StringsEs.regexInvalidMessage
        }
    }

    fun remove(id: Long) = viewModelScope.launch {
        db.prefixes().remove(id)
        refresh()
    }

    fun removeRegex(id: Long) = viewModelScope.launch {
        db.regex().remove(id)
        refreshRegex()
    }
}
