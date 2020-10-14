package com.zen.alchan.notifications

import NotificationsQuery
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.apollographql.apollo.api.Response
import com.zen.alchan.R
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.helper.enums.NotificationCategory
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.ui.main.MainActivity
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import kotlin.random.Random


class PushNotificationsService : JobIntentService() {

    private val userDataSource: UserDataSource by inject()
    private val userManager: UserManager by inject()

    companion object {
        private const val CHANNEL_ID = "Notifications"
        private const val JOB_ID = 1007
    }

    fun enqueueWork(context: Context, intent: Intent) {
        enqueueWork(context, PushNotificationsService::class.java, JOB_ID, intent)
    }

    @SuppressLint("CheckResult")
    override fun onHandleWork(intent: Intent) {
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
                        val notif = it.fragments.onAiringNotification
                        showPushNotification(notif?.id, "${notif?.contexts!![0]}${notif.episode}${notif.contexts[1]}${notif.media?.title?.userPreferred}${notif.contexts[2]}")
                    }
                    NotificationCategory.FOLLOWING_NOTIFICATION.value -> {
                        val notif = it.fragments.onFollowingNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}")
                    }
                    NotificationCategory.ACTIVITY_MESSAGE_NOTIFICATION.value -> {
                        val notif = it.fragments.onActivityMessageNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}")
                    }
                    NotificationCategory.ACTIVITY_MENTION_NOTIFICATION.value -> {
                        val notif = it.fragments.onActivityMentionNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}")
                    }
                    NotificationCategory.ACTIVITY_REPLY_NOTIFICATION.value -> {
                        val notif = it.fragments.onActivityReplyNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}")
                    }
                    NotificationCategory.ACTIVITY_REPLY_SUBSCRIBED_NOTIFICATION.value -> {
                        val notif = it.fragments.onActivityReplySubscribedNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}")
                    }
                    NotificationCategory.ACTIVITY_LIKE_NOTIFICATION.value -> {
                        val notif = it.fragments.onActivityLikeNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}")
                    }
                    NotificationCategory.ACTIVITY_REPLY_LIKE_NOTIFICATION.value -> {
                        val notif = it.fragments.onActivityReplyLikeNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}")
                    }
                    NotificationCategory.THREAD_COMMENT_MENTION_NOTIFICATION.value -> {
                        val notif = it.fragments.onThreadCommentMentionNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}${notif?.thread?.title}")
                    }
                    NotificationCategory.THREAD_COMMENT_REPLY_NOTIFICATION.value -> {
                        val notif = it.fragments.onThreadCommentReplyNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}${notif?.thread?.title}")
                    }
                    NotificationCategory.THREAD_COMMENT_SUBSCRIBED_NOTIFICATION.value -> {
                        val notif = it.fragments.onThreadCommentSubscribedNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}${notif?.thread?.title}")
                    }
                    NotificationCategory.THREAD_COMMENT_LIKE_NOTIFICATION.value -> {
                        val notif = it.fragments.onThreadCommentLikeNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}${notif?.thread?.title}")
                    }
                    NotificationCategory.THREAD_LIKE_NOTIFICATION.value -> {
                        val notif = it.fragments.onThreadLikeNotification
                        showPushNotification(notif?.id, "${notif?.user?.name}${notif?.context}${notif?.thread?.title}")
                    }
                    NotificationCategory.RELATED_MEDIA_ADDITION_NOTIFICATION.value -> {
                        val notif = it.fragments.onRelatedMediaAdditionNotification
                        showPushNotification(notif?.id, "${notif?.media?.title?.userPreferred}${notif?.context}")
                    }
                }
            }
        }

        stopSelf()
    }

    private fun showPushNotification(notificationId: Int?, message: String) {
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

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
        notificationIntent.putExtra(MainActivity.GO_TO_NOTIFICATION, true)

//        val notificationPendingIntent = TaskStackBuilder.create(this).run {
//            addNextIntentWithParentStack(notificationIntent)
//            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//        }

        val notificationPendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

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
