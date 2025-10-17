package com.humanjuan.iog26.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.humanjuan.iog26.data.DigestSettingsRepo
import com.humanjuan.iog26.digest.DailyDigestWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(ctx: Context, intent: Intent) {
        // Reprograma el resumen diario con la hora guardada
        val repo = DigestSettingsRepo(ctx)
        runBlocking {
            val s = repo.flow.first()
            if (s.enabled) DailyDigestWorker.schedule(ctx, s.hour, s.minute)
        }
    }
}
