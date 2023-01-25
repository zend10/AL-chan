package com.zen.alchan.helper.service.pushnotification

import android.content.Context
import android.os.Build
import androidx.work.*
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.utils.TimeUtil
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class DefaultPushNotificationService(
    private val context: Context,
    private val userRepository: UserRepository
): PushNotificationService {

    private val instance = WorkManager.getInstance(context)

    override fun startPushNotification() {
        var disposable: Disposable? = null
        disposable = userRepository.getAppSetting()
            .doFinally {
                disposable?.dispose()
                disposable = null
            }
            .subscribe {
                val interval = it.showPushNotificationsInterval.toLong()
                val delay = TimeUtil.getMinutesRemainingUntilTheNextHour().toLong()
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
                val workRequest = PeriodicWorkRequestBuilder<PushNotificationWorker>(interval, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .setInitialDelay(delay, TimeUnit.MINUTES)
                    .build()
                instance.enqueueUniquePeriodicWork(PUSH_NOTIFICATION_WORK, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
            }
    }

    override fun stopPushNotification() {
        instance.cancelUniqueWork(PUSH_NOTIFICATION_WORK)
    }

    companion object {
        private const val PUSH_NOTIFICATION_WORK = "pushNotificationWork"
    }
}