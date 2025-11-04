package com.humanjuan.iog26.phone

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.CallScreeningService
import android.telecom.TelecomManager
import android.telephony.PhoneNumberUtils
import android.telecom.Call.Details
import com.humanjuan.iog26.data.BlockRepository
import com.humanjuan.iog26.domain.Matching
import com.humanjuan.iog26.domain.NumberMatch

object CallPolicy {

    data class Decision(
        val block: Boolean,
        val shouldSkipCallLog: Boolean = true,
        val shouldSkipNotification: Boolean = true,
        val reason: String = ""
    )

    fun decide(
        context: Context,
        details: Details,
        repo: BlockRepository,
        blockAnonymousEnabled: Boolean,
        blockUnknownContactsEnabled: Boolean,
        skipCallLogOnBlock: Boolean,
        skipNotificationOnBlock: Boolean
    ): Decision {
        val handle: Uri? = details.handle
        val rawNumber: String? = handle?.schemeSpecificPart

        // Emergencias: nunca bloquear
        val emergency = rawNumber?.let {
            @Suppress("DEPRECATION")
            PhoneNumberUtils.isEmergencyNumber(it)
        } == true
        if (emergency) return Decision(block = false, reason = "emergency", shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)

        // Desconocidos/ocultos (sin número legible)
        // Usa flags de presentación del sistema para detectar ocultos/desconocidos
        val presentation = try { details.handlePresentation } catch (_: Throwable) { TelecomManager.PRESENTATION_ALLOWED }
        val looksAnonymous = rawNumber.isNullOrBlank() ||
                presentation == TelecomManager.PRESENTATION_RESTRICTED ||
                presentation == TelecomManager.PRESENTATION_UNKNOWN ||
                presentation == TelecomManager.PRESENTATION_PAYPHONE

        if (blockAnonymousEnabled && looksAnonymous) {
            return Decision(block = true, reason = "anonymous", shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)
        }

        // Si está habilitado, bloquear números que no estén en contactos (y no anónimos)
        if (blockUnknownContactsEnabled && !rawNumber.isNullOrBlank()) {
            val notInContacts = !isInContacts(context, rawNumber)
            if (notInContacts) {
                return Decision(block = true, reason = "unknown-contact", shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)
            }
        }

        // Regex rules (NUMBER/PREFIX) — evaluated before exact numbers and prefix rules
        val regexRules = repo.getRegexRules()
        if (!rawNumber.isNullOrBlank() && regexRules.isNotEmpty()) {
            val defaultRegion = repo.defaultRegion()
            val e164 = try { Matching.toE164(rawNumber, defaultRegion) } catch (_: Throwable) { null }
            val nsn = try { Matching.toNSN(rawNumber, defaultRegion) } catch (_: Throwable) { null }
            val digitsOnly = rawNumber.replace(Regex("[^\\d+]"), "")
            for (r in regexRules) {
                val re = try { Regex(r.pattern) } catch (_: Throwable) { null } ?: continue
                val hit = when (r.kind.uppercase()) {
                    "NUMBER" -> listOfNotNull(rawNumber, e164, digitsOnly).any { re.containsMatchIn(it) }
                    "PREFIX" -> listOfNotNull(rawNumber, e164, nsn, digitsOnly).any { re.containsMatchIn(it) }
                    else -> false
                }
                if (hit) {
                    val reason = if (r.kind.uppercase() == "NUMBER") "regex-number" else "regex-prefix"
                    return Decision(block = true, reason = reason, shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)
                }
            }
        }

        // Números exactos/NSN/short match en lista (usa heurística de libphonenumber)
        val hitNumber = if (!rawNumber.isNullOrBlank()) {
            val blocked = repo.getBlockedNumbers()
            blocked.any { stored ->
                try { NumberMatch.matches(rawNumber, stored) } catch (_: Exception) { false }
            }
        } else false
        if (hitNumber) {
            return Decision(block = true, reason = "blocked-number", shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)
        }

        // Prefijos (por país o NSN)
        val hitPrefix = repo.getPrefixes().any { rule ->
            Matching.matchesPrefix(
                rawNumber = rawNumber ?: return@any false,
                prefixDigits = rule.digits,
                countryCode = rule.countryCode,
                defaultRegion = repo.defaultRegion()
            )
        }
        if (hitPrefix) return Decision(block = true, reason = "blocked-prefix", shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)

        return Decision(block = false, shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)
    }

    fun toResponse(decision: Decision): CallScreeningService.CallResponse {
        val builder = CallScreeningService.CallResponse.Builder()
            .setDisallowCall(decision.block)
            .setRejectCall(decision.block)
            .setSkipCallLog(decision.shouldSkipCallLog)
            .setSkipNotification(decision.shouldSkipNotification)
        builder.setSilenceCall(decision.block)
        return builder.build()
    }
    
    private fun isInContacts(context: Context, number: String): Boolean {
        return try {
            val normalized = try {
                Matching.toE164(
                    number,
                    BlockRepository.get(context).defaultRegion()
                )
            } catch (_: Throwable) {
                number
            }

            if (queryPhoneLookup(context, normalized)) return true

            val simplified = normalized
                ?.replace(Regex("[^\\d+]"), "")
                ?.replace(Regex("^\\+"), "")

            if (queryPhoneLookup(context, simplified)) return true

            queryPhoneLookup(context, number)
        } catch (t: Throwable) {
            android.util.Log.w("CallPolicy", "Error checking contacts: ${t.message}")
            false
        }
    }

    private fun queryPhoneLookup(context: Context, raw: String?): Boolean {
        if (raw.isNullOrBlank()) return false
        return try {
            val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI
                .buildUpon()
                .appendPath(Uri.encode(raw))
                .build()
            context.contentResolver.query(
                uri,
                arrayOf(ContactsContract.PhoneLookup._ID),
                null,
                null,
                null
            )?.use { cursor ->
                cursor.moveToFirst()
            } ?: false
        } catch (_: Throwable) {
            false
        }
    }
}
