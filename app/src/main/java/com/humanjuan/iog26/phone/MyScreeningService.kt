package com.humanjuan.iog26.phone

import android.telecom.CallScreeningService
import android.util.Log
import com.humanjuan.iog26.data.AppDb
import com.humanjuan.iog26.data.BlockRepository
import com.humanjuan.iog26.data.BlockedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MyScreeningService : CallScreeningService() {

    private val tag = "MyScreeningService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onScreenCall(callDetails: android.telecom.Call.Details) {
        try {
            val repo = BlockRepository.get(applicationContext)

            // Leer ajustes actuales desde Room (bloqueo, flags de log/notif)
            val db = AppDb.get(applicationContext)
            val settings = kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
                db.settings().get() ?: com.humanjuan.iog26.data.Settings()
            }

            val decision = CallPolicy.decide(
                context = applicationContext,
                details = callDetails,
                repo = repo,
                blockUnknownEnabled = settings.blockUnknownEnabled,
                skipCallLogOnBlock = settings.skipCallLogOnBlock,
                skipNotificationOnBlock = settings.skipNotificationOnBlock
            )
            Log.d(tag, "decision=$decision")

            if (decision.block) {
                val number = callDetails.handle?.schemeSpecificPart
                // <-- AQUÍ lanzamos la suspend function en IO
                serviceScope.launch {
                    try {
                        val db = AppDb.get(applicationContext)
                        db.events().add(
                            BlockedEvent(
                                e164 = number,
                                ts = System.currentTimeMillis()
                            )
                        )
                    } catch (t: Throwable) {
                        Log.w(tag, "No se pudo registrar evento", t)
                    }
                }
            }

            respondToCall(callDetails, CallPolicy.toResponse(decision))

        } catch (t: Throwable) {
            Log.e(tag, "onScreenCall error", t)
            respondToCall(callDetails, CallResponse.Builder().build())
        }
    }
}
