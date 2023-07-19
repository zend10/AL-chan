package com.zen.alchan.helper.service.pushnotification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.utils.TimeUtil
import io.reactivex.rxjava3.disposables.Disposable

class DefaultPushNotificationService(
    private val context: Context,
    private val userRepository: UserRepository
): PushNotificationService {

    override fun startPushNotification() {
        var disposable: Disposable? = null
        disposable = userRepository.getAppSetting()
            .doFinally {
                disposable?.dispose()
                disposable = null
            }
            .subscribe { appSetting ->
                stopPushNotification()

                if (!isPushNotificationActivated(appSetting) || !isPushNotificationAllowed()) {
                    return@subscribe
                }

                setBroadcastReceiverOnBoot(true)

                val minDelay = 10 * 60 * 1000 // inexact alarm minimum start is 10 mins
                val delay = (TimeUtil.getMinutesRemainingUntilTheNextHour().toLong() * 60 * 1000).let {
                    if (it >= minDelay) it else it + minDelay
                }
                val alarmManager = getAlarmManager()
                val alarmIntent = getAlarmPendingIntent()

                alarmManager?.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    delay,
                    AlarmManager.INTERVAL_HOUR,
                    alarmIntent
                )
            }
    }

    private fun stopPushNotification() {
        setBroadcastReceiverOnBoot(false)
        val alarmManager = getAlarmManager()
        val alarmIntent = getAlarmPendingIntent()
        alarmManager?.cancel(alarmIntent)
    }

    private fun isPushNotificationActivated(appSetting: AppSetting): Boolean {
        return appSetting.sendFollowsPushNotifications ||
                appSetting.sendRelationsPushNotifications ||
                appSetting.sendForumPushNotifications ||
                appSetting.sendActivityPushNotifications ||
                appSetting.sendAiringPushNotifications
    }

    private fun isPushNotificationAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.areNotificationsEnabled() && notificationManager.notificationChannels.none { it.importance == NotificationManagerCompat.IMPORTANCE_NONE }
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    private fun isAlarmAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    private fun getAlarmManager(): AlarmManager? {
        return context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    private fun getAlarmPendingIntent(): PendingIntent {
        return Intent(context, PushNotificationBroadcastReceiver::class.java).let { intent ->
            intent.action = PushNotificationBroadcastReceiver.PUSH_NOTIFICATION_ACTION
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private fun setBroadcastReceiverOnBoot(enabled: Boolean) {
        val receiver = ComponentName(context, PushNotificationBroadcastReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}