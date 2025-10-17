package com.humanjuan.iog26.data

import android.content.Context
import java.util.concurrent.atomic.AtomicLong

/**
 * Repositorio mínimo para pruebas de CallScreening.
 * - defaultRegion(): región ISO por defecto (ej: "CL"), null para global.
 * - blockUnknownEnabled(): toggle para bloquear números desconocidos.
 * - isBlockedNumber(e164): set exacto de números bloqueados en formato +E164.
 * - getPrefixes(): reglas de prefijo (countryCode = null => regla sobre NSN).
 */
interface BlockRepository {
    fun defaultRegion(): String?
    fun blockUnknownEnabled(): Boolean
    fun isBlockedNumber(e164: String): Boolean
    fun getBlockedNumbers(): List<String>
    fun getPrefixes(): List<PrefixRule>

    companion object {
        /** Obtiene una implementación respaldada por Room para usar las mismas reglas que la UI. */
        fun get(context: Context): BlockRepository = RoomBackedBlockRepository(context)
    }
}

/** Regla de prefijo. countryCode == null => regla sobre NSN (país opcional / en blanco). */
data class PrefixRule(
    val id: Long,
    val digits: String,
    val countryCode: String? = null
)

/** Implementación simple en memoria (para desarrollo/pruebas). */
private object InMemoryBlockRepository : BlockRepository {
    // Región por defecto (ajústalo si quieres pruebas localizadas)
    private var region: String? = null

    private val numbers = mutableSetOf<String>() // +E164
    private val prefixes = mutableListOf<PrefixRule>()
    private val idGen = AtomicLong(1L)

    // Toggle de “bloquear desconocidos”
    private var blockUnknown = false

    override fun defaultRegion(): String? = region
    override fun blockUnknownEnabled(): Boolean = blockUnknown

    override fun isBlockedNumber(e164: String): Boolean = numbers.contains(e164)
    override fun getBlockedNumbers(): List<String> = numbers.toList()

    override fun getPrefixes(): List<PrefixRule> = prefixes.toList()

    // ------- Helpers para pruebas desde la UI / debug (opcionales) -------

    fun setDefaultRegion(value: String?) { region = value }
    fun setBlockUnknown(enabled: Boolean) { blockUnknown = enabled }

    fun addBlockedNumber(e164: String) { numbers += e164 }
    fun removeBlockedNumber(e164: String) { numbers -= e164 }

    fun addPrefix(digits: String, countryCode: String?): PrefixRule {
        val rule = PrefixRule(id = idGen.getAndIncrement(), digits = digits, countryCode = countryCode?.ifBlank { null })
        prefixes += rule
        return rule
    }
    fun removePrefix(id: Long) { prefixes.removeAll { it.id == id } }

    // Seed opcional para que tengas algo al iniciar
    init {
        // Ejemplo: bloquear prefijo 800 sobre NSN, y un número E164 cualquiera
        addPrefix(digits = "800", countryCode = null)
        addBlockedNumber("+56987654321")
        setBlockUnknown(false)
        setDefaultRegion("CL") // pon "CL" si quieres inferencias locales
    }
}

/** Implementación respaldada por Room para unificar reglas con la UI. */
private class RoomBackedBlockRepository(ctx: Context) : BlockRepository {
    private val appCtx = ctx.applicationContext

    override fun defaultRegion(): String? = "CL" // región por defecto para parseo de números sin +CC

    override fun blockUnknownEnabled(): Boolean = kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
        val s = AppDb.get(appCtx).settings().get()
        s?.blockUnknownEnabled ?: true
    }

    override fun isBlockedNumber(e164: String): Boolean = kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
        val list = AppDb.get(appCtx).numbers().all()
        list.any { it.e164 == e164 }
    }

    override fun getBlockedNumbers(): List<String> = kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
        AppDb.get(appCtx).numbers().all().map { it.e164 }
    }

    override fun getPrefixes(): List<PrefixRule> = kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
        val list = AppDb.get(appCtx).prefixes().all()
        list.map { dbRule ->
            PrefixRule(
                id = dbRule.id,
                digits = dbRule.prefixDigits,
                countryCode = dbRule.countryCode?.toString()
            )
        }
    }
}
