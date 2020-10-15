package com.zen.alchan.notifications

import NotificationsQuery
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.apollographql.apollo.api.Response
import com.zen.alchan.R
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.localstorage.AppSettingsManager
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.helper.enums.NotificationCategory
import com.zen.alchan.ui.main.MainActivity
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import kotlin.random.Random


class PushNotificationsService : JobIntentService() {

    private val userDataSource: UserDataSource by inject()
    private val userManager: UserManager by inject()
    private val appSettingManager: AppSettingsManager by inject()

    companion object {
        private const val CHANNEL_ID = "Notifications"
        private const val JOB_ID = 1007
    }

    fun enqueueWork(context: Context, intent: Intent) {
        enqueueWork(context, PushNotificationsService::class.java, JOB_ID, intent)
    }

    @SuppressLint("CheckResult")
    override fun onHandleWork(intent: Intent) {
        if (appSettingManager.appSettings.sendAiringPushNotification == false &&
            appSettingManager.appSettings.sendActivityPushNotification == false &&
            appSettingManager.appSettings.sendForumPushNotification == false &&
            appSettingManager.appSettings.sendFollowsPushNotification == false &&
            appSettingManager.appSettings.sendRelationsPushNotification == false
        ) {
            return
        }

        userDataSource.getNotification(1, null, false).subscribeWith(@SuppressLint("CheckResult")
        object : Observer<Response<NotificationsQuery.Data>> {
            override fun onSubscribe(d: Disposable) {
                // do nothing
            }

            override fun onNext(t: Response<NotificationsQuery.Data>) {
                handleResult(t)
            }

            override fun onComplete() {
                // do nothing
            }

            override fun onError(e: Throwable) {
                stopSelf()
            }
        })
    }

    private fun handleResult(response: Response<NotificationsQuery.Data>) {
        if (!response.hasErrors()) {
            val notificationList = if (userManager.latestNotification == null) {
                response.data?.page?.notifications
            } else {
                response.data?.page?.notifications?.reversed()
            }

            notificationList?.forEach {
                when (it?.__typename) {
                    NotificationCategory.AIRING_NOTIFICATION.value -> {
                        val item = it.fragments.onAiringNotification
                        val message = "${item?.contexts!![0]}${item.episode}${item.contexts[1]}${item.media?.title?.userPreferred}${item.contexts[2]}"
                        showPushNotification(item.id, message, NotificationCategory.AIRING_NOTIFICATION)
                    }
                    NotificationCategory.FOLLOWING_NOTIFICATION.value -> {
                        val item = it.fragments.onFollowingNotification
                        val message = "${item?.user?.name}${item?.context}"
                        showPushNotification(item?.id, message, NotificationCategory.FOLLOWING_NOTIFICATION)
                    }
                    NotificationCategory.ACTIVITY_MESSAGE_NOTIFICATION.value -> {
                        val item = it.fragments.onActivityMessageNotification
                        val message = "${item?.user?.name}${item?.context}"
                        showPushNotification(item?.id, message, NotificationCategory.ACTIVITY_MESSAGE_NOTIFICATION)
                    }
                    NotificationCategory.ACTIVITY_MENTION_NOTIFICATION.value -> {
                        val item = it.fragments.onActivityMentionNotification
                        val message = "${item?.user?.name}${item?.context}"
                        showPushNotification(item?.id, message, NotificationCategory.ACTIVITY_MENTION_NOTIFICATION)
                    }
                    NotificationCategory.ACTIVITY_REPLY_NOTIFICATION.value -> {
                        val item = it.fragments.onActivityReplyNotification
                        val message = "${item?.user?.name}${item?.context}"
                        showPushNotification(item?.id, message, NotificationCategory.ACTIVITY_REPLY_NOTIFICATION)
                    }
                    NotificationCategory.ACTIVITY_REPLY_SUBSCRIBED_NOTIFICATION.value -> {
                        val item = it.fragments.onActivityReplySubscribedNotification
                        val message = "${item?.user?.name}${item?.context}"
                        showPushNotification(item?.id, message, NotificationCategory.ACTIVITY_REPLY_SUBSCRIBED_NOTIFICATION)
                    }
                    NotificationCategory.ACTIVITY_LIKE_NOTIFICATION.value -> {
                        val item = it.fragments.onActivityLikeNotification
                        val message = "${item?.user?.name}${item?.context}"
                        showPushNotification(item?.id, message, NotificationCategory.ACTIVITY_LIKE_NOTIFICATION)
                    }
                    NotificationCategory.ACTIVITY_REPLY_LIKE_NOTIFICATION.value -> {
                        val item = it.fragments.onActivityReplyLikeNotification
                        val message = "${item?.user?.name}${item?.context}"
                        showPushNotification(item?.id, message, NotificationCategory.ACTIVITY_REPLY_LIKE_NOTIFICATION)
                    }
                    NotificationCategory.THREAD_COMMENT_MENTION_NOTIFICATION.value -> {
                        val item = it.fragments.onThreadCommentMentionNotification
                        val message = "${item?.user?.name}${item?.context}${item?.thread?.title}"
                        showPushNotification(item?.id, message, NotificationCategory.THREAD_COMMENT_MENTION_NOTIFICATION)
                    }
                    NotificationCategory.THREAD_COMMENT_REPLY_NOTIFICATION.value -> {
                        val item = it.fragments.onThreadCommentReplyNotification
                        val message = "${item?.user?.name}${item?.context}${item?.thread?.title}"
                        showPushNotification(item?.id, message, NotificationCategory.THREAD_COMMENT_REPLY_NOTIFICATION)
                    }
                    NotificationCategory.THREAD_COMMENT_SUBSCRIBED_NOTIFICATION.value -> {
                        val item = it.fragments.onThreadCommentSubscribedNotification
                        val message = "${item?.user?.name}${item?.context}${item?.thread?.title}"
                        showPushNotification(item?.id, message, NotificationCategory.THREAD_COMMENT_SUBSCRIBED_NOTIFICATION)
                    }
                    NotificationCategory.THREAD_COMMENT_LIKE_NOTIFICATION.value -> {
                        val item = it.fragments.onThreadCommentLikeNotification
                        val message = "${item?.user?.name}${item?.context}${item?.thread?.title}"
                        showPushNotification(item?.id, message, NotificationCategory.THREAD_COMMENT_LIKE_NOTIFICATION)
                    }
                    NotificationCategory.THREAD_LIKE_NOTIFICATION.value -> {
                        val item = it.fragments.onThreadLikeNotification
                        val message = "${item?.user?.name}${item?.context}${item?.thread?.title}"
                        showPushNotification(item?.id, message, NotificationCategory.THREAD_LIKE_NOTIFICATION)
                    }
                    NotificationCategory.RELATED_MEDIA_ADDITION_NOTIFICATION.value -> {
                        val item = it.fragments.onRelatedMediaAdditionNotification
                        val message = "${item?.media?.title?.userPreferred}${item?.context}"
                        showPushNotification(item?.id, message, NotificationCategory.RELATED_MEDIA_ADDITION_NOTIFICATION)
                    }
                }
            }
        }

        stopSelf()
    }

    private fun showPushNotification(notificationId: Int?, message: String, notificationCategory: NotificationCategory) {
        if (userManager.latestNotification == null && notificationId != null) {
            userManager.setLatestNotification(notificationId)
            return
        }

        if (userManager.latestNotification != null && notificationId != null && userManager.latestNotification!! >= notificationId) {
            return
        }

        if (notificationId != null) {
            userManager.setLatestNotification(notificationId)
        }

        when (notificationCategory) {
            NotificationCategory.AIRING_NOTIFICATION -> {
                if (appSettingManager.appSettings.sendAiringPushNotification == false) return
            }
            NotificationCategory.FOLLOWING_NOTIFICATION -> {
                if (appSettingManager.appSettings.sendFollowsPushNotification == false) return
            }
            NotificationCategory.ACTIVITY_MESSAGE_NOTIFICATION,
            NotificationCategory.ACTIVITY_MENTION_NOTIFICATION,
            NotificationCategory.ACTIVITY_REPLY_NOTIFICATION,
            NotificationCategory.ACTIVITY_REPLY_SUBSCRIBED_NOTIFICATION,
            NotificationCategory.ACTIVITY_LIKE_NOTIFICATION,
            NotificationCategory.ACTIVITY_REPLY_LIKE_NOTIFICATION -> {
                if (appSettingManager.appSettings.sendActivityPushNotification == false) return
            }
            NotificationCategory.THREAD_COMMENT_MENTION_NOTIFICATION,
            NotificationCategory.THREAD_COMMENT_REPLY_NOTIFICATION,
            NotificationCategory.THREAD_COMMENT_SUBSCRIBED_NOTIFICATION,
            NotificationCategory.THREAD_COMMENT_LIKE_NOTIFICATION,
            NotificationCategory.THREAD_LIKE_NOTIFICATION -> {
                if (appSettingManager.appSettings.sendForumPushNotification == false) return
            }
            NotificationCategory.RELATED_MEDIA_ADDITION_NOTIFICATION -> {
                if (appSettingManager.appSettings.sendRelationsPushNotification == false) return
            }
        }

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        notificationIntent.putExtra(MainActivity.GO_TO_NOTIFICATION, true)

        val notificationPendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(message)
            .setColorized(true)
            .setColor(ContextCompat.getColor(applicationContext, R.color.yellow))
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notificationPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notifications"
            val descriptionText = "AniList Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(Random.nextInt(), builder.build())
        }
    }
}
