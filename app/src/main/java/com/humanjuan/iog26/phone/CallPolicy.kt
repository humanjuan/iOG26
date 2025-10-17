package com.humanjuan.iog26.phone

import android.content.Context
import android.net.Uri
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
        blockUnknownEnabled: Boolean,
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

        if (blockUnknownEnabled && looksAnonymous) {
            return Decision(block = true, reason = "unknown", shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)
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

        // Permitir por defecto
        return Decision(block = false, shouldSkipCallLog = skipCallLogOnBlock, shouldSkipNotification = skipNotificationOnBlock)
    }

    fun toResponse(decision: Decision): CallScreeningService.CallResponse {
        val builder = CallScreeningService.CallResponse.Builder()
            .setDisallowCall(decision.block)
            .setRejectCall(decision.block)
            .setSkipCallLog(decision.shouldSkipCallLog)
            .setSkipNotification(decision.shouldSkipNotification)

        // En algunos OEMs, silenciar además de rechazar mejora la fiabilidad del bloqueo.
        // Usamos reflexión para evitar advertencias de API (setSilenceCall existe desde API 29).
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                val m = builder.javaClass.getMethod("setSilenceCall", Boolean::class.javaPrimitiveType)
                m.invoke(builder, decision.block)
            } catch (_: Throwable) { /* ignore */ }
        }
        return builder.build()
    }
}
