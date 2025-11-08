package com.humanjuan.iog26.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.humanjuan.iog26.data.AppDb
import com.humanjuan.iog26.data.BlockedNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiBlockedNumber(
    val e164: String,
    val createdAt: Long
)

class NumbersViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDb.get(app)
    private val _items = MutableStateFlow<List<UiBlockedNumber>>(emptyList())
    val items: StateFlow<List<UiBlockedNumber>> = _items

    data class UiRegexRule(val id: Long, val pattern: String, val createdAt: Long)
    private val _regexItems = MutableStateFlow<List<UiRegexRule>>(emptyList())
    val regexItems: StateFlow<List<UiRegexRule>> = _regexItems

    var defaultRegion: String = com.humanjuan.iog26.data.BlockRepository.get(app).defaultRegion() ?: "ZZ"

    init { refresh(); refreshRegex() }

    fun refresh() = viewModelScope.launch {
        _items.value = db.numbers().all()
            .map { UiBlockedNumber(it.e164, it.createdAt) }
            .sortedBy { it.e164 }
    }

    fun refreshRegex() = viewModelScope.launch {
        _regexItems.value = db.regex().allByKind("NUMBER").map { UiRegexRule(it.id, it.pattern, it.createdAt) }
    }

    fun add(raw: String): String? = try {
        val util = PhoneNumberUtil.getInstance()
        val proto = util.parse(raw, defaultRegion)
        val e164 = util.format(proto, PhoneNumberUtil.PhoneNumberFormat.E164)
        viewModelScope.launch {
            db.numbers().add(BlockedNumber(e164, createdAt = System.currentTimeMillis()))
            refresh()
        }
        null
    } catch (e: Exception) {
        e.message ?: "Número inválido"
    }

    fun addRegex(pattern: String): String? {
        return try {
            Regex(pattern) // valida sintaxis
            viewModelScope.launch {
                db.regex().add(com.humanjuan.iog26.data.RegexRule(
                    kind = "NUMBER",
                    pattern = pattern,
                    createdAt = System.currentTimeMillis()
                ))
                refreshRegex()
            }
            null
        } catch (t: Throwable) {
            android.util.Log.w("NumbersViewModel", "Invalid regex pattern sha1=${sha1(pattern)}: ${t.message}")
            friendlyRegexError(t)
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
            android.util.Log.w("NumbersViewModel", "Invalid regex (update) sha1=${sha1(pattern)}: ${t.message}")
            friendlyRegexError(t)
        }
    }

    private fun sha1(s: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-1")
        val bytes = md.digest(s.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun friendlyRegexError(t: Throwable): String {
        val m = t.message?.lowercase().orEmpty()
        return when {
            "look-behind" in m && "invalid" in m -> "Look-behind no soportado o de longitud variable"
            "dangling meta character" in m || "quantifier" in m -> "Cuantificador inválido"
            "unclosed" in m || ("missing" in m && ")" in m) -> "Paréntesis sin cerrar"
            else -> com.humanjuan.iog26.ui.theme.StringsEs.regexInvalidMessage
        }
    }

    fun remove(e164: String) = viewModelScope.launch {
        db.numbers().remove(e164)
        refresh()
    }

    fun removeRegex(id: Long) = viewModelScope.launch {
        db.regex().remove(id)
        refreshRegex()
    }
}
