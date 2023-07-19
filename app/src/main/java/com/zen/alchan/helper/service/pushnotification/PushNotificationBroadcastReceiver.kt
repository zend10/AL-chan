package com.zen.alchan.helper.service.pushnotification

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import org.koin.java.KoinJavaComponent.inject

class PushNotificationBroadcastReceiver : BroadcastReceiver() {

    private val pushNotificationService: PushNotificationService by inject(PushNotificationService::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null &&
            listOf(
                Intent.ACTION_BOOT_COMPLETED,
                AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
            ).any { intent.action == it }
        ) {
            pushNotificationService.startPushNotification()
        } else if (context != null && intent != null && intent.action == PUSH_NOTIFICATION_ACTION) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<PushNotificationWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(PUSH_NOTIFICATION_WORK, ExistingWorkPolicy.REPLACE, workRequest)
        }
    }

    companion object {
        private const val PUSH_NOTIFICATION_WORK = "pushNotificationWork"
        const val PUSH_NOTIFICATION_ACTION = "pushNotificationAction"
    }
}