package com.humanjuan.iog26.digest

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.humanjuan.iog26.data.AppDb
import java.time.Duration
import java.time.ZonedDateTime

class DailyDigestWorker(
    appCtx: android.content.Context,
    params: WorkerParameters
) : CoroutineWorker(appCtx, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val db = AppDb.get(applicationContext)
        val now = ZonedDateTime.now()
        val start = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
        val events = db.events().since(start.toInstant().toEpochMilli())
        if (events.isNotEmpty()) {
            show(events.map { it.e164 ?: "Desconocido" })
        }
        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun show(blocked: List<String>) {
        val channelId = "CALL_FILTER_DIGEST"
        if (Build.VERSION.SDK_INT >= 26) {
            val mgr = applicationContext.getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(
                NotificationChannel(channelId, "Resumen de bloqueos", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val top = blocked.distinct().take(5).joinToString("\n")
        val more = if (blocked.size > 5) "\n… y ${blocked.size - 5} más" else ""
        val notif = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("Llamadas bloqueadas hoy: ${blocked.size}")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$top$more"))
            .build()
        NotificationManagerCompat.from(applicationContext).notify(1001, notif)
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun schedule(ctx: android.content.Context, hour: Int, minute: Int) {
            val now = ZonedDateTime.now()
            var next = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
            if (!next.isAfter(now)) next = next.plusDays(1)
            val delay = Duration.between(now, next)

            val req = PeriodicWorkRequestBuilder<DailyDigestWorker>(Duration.ofDays(1))
                .setInitialDelay(delay)
                .addTag("daily_digest")
                .build()

            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "daily_digest",
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }
    }
}
