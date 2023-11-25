package com.zen.alchan.helper.service.pushnotification

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.utils.PushNotificationUtil
import io.reactivex.rxjava3.core.Single
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random

class PushNotificationWorker(private val context: Context, workerParameters: WorkerParameters) : RxWorker(context, workerParameters) {

    private val userRepository: UserRepository by inject(UserRepository::class.java)

    override fun createWork(): Single<Result> {
        val notificationsSingle = Single.fromObservable(userRepository.getNotifications(1, null, false))
        val unreadNotificationCountSingle = Single.fromObservable(userRepository.getLatestUnreadNotificationCount())
        val appSettingSingle = Single.fromObservable(userRepository.getAppSetting())
        val lastNotificationIdSingle = Single.fromObservable(userRepository.getLastNotificationId())
        return Single.zip(
            notificationsSingle,
            unreadNotificationCountSingle,
            appSettingSingle,
            lastNotificationIdSingle
        ) { notificationData, unreadNotificationCount, appSetting, lastNotificationId ->
            PushNotificationParam(notificationData.page.data, unreadNotificationCount, appSetting, lastNotificationId)
        }.flatMap { param ->
            showPushNotification(param.notifications, param.unreadNotificationCount, param.appSetting, param.lastNotificationId)
            Single.just(Result.success())
        }
    }

    private fun showPushNotification(notifications: List<Notification>, unreadNotificationCount: Int, appSetting: AppSetting, lastNotificationId: Int) {
        userRepository.setLastNotificationId(notifications.first().id)

        // check if should show push notification from AppSetting
        if (
            !appSetting.sendAiringPushNotifications &&
            !appSetting.sendActivityPushNotifications &&
            !appSetting.sendForumPushNotifications &&
            !appSetting.sendFollowsPushNotifications &&
            !appSetting.sendRelationsPushNotifications
        )
            return

         val unreadNotifications = notifications.take(unreadNotificationCount)
         if (unreadNotifications.isEmpty())
             return

        // check if there is any new notification
        val isNewNotificationAvailable = (unreadNotifications.firstOrNull()?.id ?: 0) > lastNotificationId
        if (!isNewNotificationAvailable)
            return

        val notificationsToBeShown = unreadNotifications.filter {
            if (it.id <= lastNotificationId)
                return@filter false

            var isIncludedNotification = false

            if (appSetting.sendAiringPushNotifications && it is AiringNotification)
                isIncludedNotification = true

            if (
                appSetting.sendActivityPushNotifications &&
                (
                    it is ActivityMessageNotification ||
                    it is ActivityMentionNotification ||
                    it is ActivityReplyNotification ||
                    it is ActivityReplySubscribedNotification ||
                    it is ActivityLikeNotification ||
                    it is ActivityReplyLikeNotification
                )
            )
                isIncludedNotification = true

            if (
                appSetting.sendForumPushNotifications &&
                (
                    it is ThreadCommentMentionNotification ||
                    it is ThreadCommentReplyNotification ||
                    it is ThreadCommentSubscribedNotification ||
                    it is ThreadCommentLikeNotification ||
                    it is ThreadLikeNotification
                )
            )
                isIncludedNotification = true

            if (appSetting.sendFollowsPushNotifications && it is FollowingNotification)
                isIncludedNotification = true

            if (
                appSetting.sendRelationsPushNotifications &&
                (
                    it is RelatedMediaAdditionNotification ||
                    it is MediaDataChangeNotification ||
                    it is MediaMergeNotification ||
                    it is MediaDeletionNotification
                )
            )
                isIncludedNotification = true

            return@filter isIncludedNotification
        }

        // check if there is any allowed push notification
        if (notificationsToBeShown.isEmpty())
            return

        // check if should merge push notification into 1
        if (appSetting.mergePushNotifications && notificationsToBeShown.size > 1) {
            PushNotificationUtil.createPushNotificationWithDefaultId(context, context.getString(R.string.you_have_unread_notifications))
            return
        }

        // show each notification one by one
        notificationsToBeShown.forEach { notification ->
            PushNotificationUtil.createPushNotification(context, Random.nextInt(), notification.getMessage(appSetting))
        }
    }
}