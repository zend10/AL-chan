package com.zen.alchan.helper.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.zen.alchan.R
import com.zen.alchan.ui.deeplink.DeepLinkActivity

object PushNotificationUtil {

    private const val CHANNEL_ID = "Notifications"
    private const val MERGED_NOTIFICATION_ID = 1017

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getNotificationManager(context)
            val name = "Notifications"
            val descriptionText = "AniList Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createPushNotificationWithDefaultId(context: Context, message: String) {
        createPushNotification(context, MERGED_NOTIFICATION_ID, message)
    }

    fun createPushNotification(context: Context, id: Int, message: String) {
        val notificationIntent = Intent(context, DeepLinkActivity::class.java)
        notificationIntent.data = DeepLink.generateNotifications().uri
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = getNotificationManager(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message)
            .setColorized(true)
            .setColor(ContextCompat.getColor(context, R.color.yellow))
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)

        createNotificationChannel(context)

        notificationManager.notify(id, builder.build())
    }

    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}