package com.humanjuan.iog26.domain

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
//import com.humanjuan.lib.safeLet
import com.humanjuan.iog26.data.BlockedPrefixRule
import com.humanjuan.iog26.data.PrefixScope

// Pequeño util opcional (evita warnings por nulls)
//private inline fun <A, B, R> safeLet(a: A?, b: B?, block: (A, B) -> R): R? =
//    if (a != null && b != null) block(a, b) else null

/**
 * Adaptador simple para el código que importa com.humanjuan.iog26.domain.Matching.
 * Ofrece helpers neutrales (E.164 / NSN / CC / match por prefijo).
 */
object Matching {
    private val util = PhoneNumberUtil.getInstance()

    /** Normaliza a E.164. Si no hay región, usa "ZZ" (unknown region) para permitir +CC. */
    fun toE164(raw: String, defaultRegion: String?): String? = try {
        val region = defaultRegion ?: "ZZ"
        val p = util.parse(raw, region)
        if (util.isValidNumber(p))
            util.format(p, PhoneNumberUtil.PhoneNumberFormat.E164)
        else null
    } catch (_: Exception) { null }

    /** Devuelve el NSN (parte nacional) o null si no se puede parsear. */
    fun toNSN(raw: String, defaultRegion: String?): String? = try {
        val region = defaultRegion ?: "ZZ"
        val p = util.parse(raw, region)
        util.getNationalSignificantNumber(p)
    } catch (_: Exception) { null }

    /** Devuelve el código de país si es reconocible. */
    fun countryCode(raw: String, defaultRegion: String?): Int? = try {
        val region = defaultRegion ?: "ZZ"
        val p = util.parse(raw, region)
        p.countryCode
    } catch (_: Exception) { null }

    /**
     * Chequea si un número matchea un prefijo.
     * - countryCode == null  -> regla sobre NSN (funciona con o sin +CC en el entrante)
     * - countryCode != null  -> exige que el número sea de ese país y aplique el prefijo sobre el NSN
     */
    fun matchesPrefix(
        rawNumber: String,
        prefixDigits: String,
        countryCode: String?,
        defaultRegion: String?
    ): Boolean = try {
        val region = defaultRegion ?: "ZZ"
        val p = util.parse(rawNumber, region)
        val nsn = util.getNationalSignificantNumber(p)
        val cc = countryCode?.removePrefix("+")?.trim()?.takeIf { it.isNotEmpty() }
        if (cc == null) {
            nsn.startsWith(prefixDigits)
        } else {
            (p.countryCode.toString() == cc) && nsn.startsWith(prefixDigits)
        }
    } catch (_: NumberParseException) { false }
}

object NumberMatch {
    private val util = PhoneNumberUtil.getInstance()

    fun normalizeToE164(input: String, defaultRegion: String): String? = try {
        val p = util.parse(input, defaultRegion)
        util.format(p, PhoneNumberUtil.PhoneNumberFormat.E164)
    } catch (_: Exception) { null }

    fun matches(incomingRaw: String, blockedE164: String): Boolean {
        val mt = util.isNumberMatch(incomingRaw, blockedE164)
        return mt == PhoneNumberUtil.MatchType.EXACT_MATCH ||
                mt == PhoneNumberUtil.MatchType.NSN_MATCH ||
                mt == PhoneNumberUtil.MatchType.SHORT_NSN_MATCH
    }
}

/**
 * Prefijos: soporta reglas BY_COUNTRY (+CC + prefijo) y NATIONAL (prefijo sobre NSN).
 * Si el entrante viene con o sin +CC, libphonenumber nos da el NSN y el CC correctos.
 */
class PrefixMatcher(
    private val rules: List<BlockedPrefixRule>,
    private val defaultRegion: String
) {
    private val util = PhoneNumberUtil.getInstance()

    fun isBlocked(incomingRaw: String): Boolean {
        val proto = try { util.parse(incomingRaw, defaultRegion) } catch (_: Exception) { return false }
        val cc = proto.countryCode
        val nsn = util.getNationalSignificantNumber(proto)

        for (r in rules) {
            when (r.scope) {
                PrefixScope.BY_COUNTRY -> if (r.countryCode == cc && nsn.startsWith(r.prefixDigits)) return true
                PrefixScope.NATIONAL   -> if (nsn.startsWith(r.prefixDigits)) return true
            }
        }
        return false
    }
}
