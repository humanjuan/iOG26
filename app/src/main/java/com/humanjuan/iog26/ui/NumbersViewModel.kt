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

    var defaultRegion: String = "CL"

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _items.value = db.numbers().all()
            .map { UiBlockedNumber(it.e164, it.createdAt) }
            .sortedBy { it.e164 }
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

    fun remove(e164: String) = viewModelScope.launch {
        db.numbers().remove(e164)
        refresh()
    }
}
