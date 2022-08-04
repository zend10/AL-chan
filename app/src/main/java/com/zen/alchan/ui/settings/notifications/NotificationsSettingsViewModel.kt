package com.zen.alchan.ui.settings.notifications

import com.zen.alchan.R
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.NotificationOption
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import type.NotificationType

class NotificationsSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

    private val _activityReply = BehaviorSubject.createDefault(false)
    val activityReply: Observable<Boolean>
        get() = _activityReply

    private val _activityReplySubscribed = BehaviorSubject.createDefault(false)
    val activityReplySubscribed: Observable<Boolean>
        get() = _activityReplySubscribed

    private val _following = BehaviorSubject.createDefault(false)
    val following: Observable<Boolean>
        get() = _following

    private val _activityMessage = BehaviorSubject.createDefault(false)
    val activityMessage: Observable<Boolean>
        get() = _activityMessage

    private val _activityMention = BehaviorSubject.createDefault(false)
    val activityMention: Observable<Boolean>
        get() = _activityMention

    private val _activityLike = BehaviorSubject.createDefault(false)
    val activityLike: Observable<Boolean>
        get() = _activityLike

    private val _activityReplyLike = BehaviorSubject.createDefault(false)
    val activityReplyLike: Observable<Boolean>
        get() = _activityReplyLike

    private val _threadCommentReply = BehaviorSubject.createDefault(false)
    val threadCommentReply: Observable<Boolean>
        get() = _threadCommentReply

    private val _threadCommentMention = BehaviorSubject.createDefault(false)
    val threadCommentMention: Observable<Boolean>
        get() = _threadCommentMention

    private val _threadCommentLike = BehaviorSubject.createDefault(false)
    val threadCommentLike: Observable<Boolean>
        get() = _threadCommentLike

    private val _threadSubscribed = BehaviorSubject.createDefault(false)
    val threadSubscribed: Observable<Boolean>
        get() = _threadSubscribed

    private val _threadLike = BehaviorSubject.createDefault(false)
    val threadLike: Observable<Boolean>
        get() = _threadLike

    private var currentNotificationOptions: ArrayList<NotificationOption>? = null

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                userRepository.getViewer(Source.CACHE)
                    .applyScheduler()
                    .subscribe { user ->
                        currentNotificationOptions = ArrayList(user.options.notificationOptions)

                        /*
                            Automatically subscribe me to activity I create -> ACTIVITY_REPLY
                            Automatically subscribe me to activity I reply to -> ACTIVITY_REPLY_SUBSCRIBED
                            When someone follows me -> FOLLOWING
                            When I receive message -> ACTIVITY_MESSAGE
                            When I am @ mentioned in an activity or activity reply -> ACTIVITY_MENTION
                            When someone likes my activity -> ACTIVITY_LIKE
                            When someone likes my activity reply -> ACTIVITY_REPLY_LIKE
                            When someone replies to my forum comment -> THREAD_COMMENT_REPLY
                            When I am @ mentioned in a forum comment -> THREAD_COMMENT_MENTION
                            When someone likes my forum comment -> THREAD_COMMENT_LIKE
                            When someone replies to a forum thread I'm subscribed to -> THREAD_SUBSCRIBED
                            When someone likes my forum thread -> THREAD_LIKE
                            AIRING and RELATED_MEDIA_ADDITION are not in AniList notification settings
                         */

                        updateNotificationOption(NotificationType.ACTIVITY_REPLY, currentNotificationOptions?.find { it.type == NotificationType.ACTIVITY_REPLY }?.enabled ?: false)
                        updateNotificationOption(NotificationType.ACTIVITY_REPLY_SUBSCRIBED, currentNotificationOptions?.find { it.type == NotificationType.ACTIVITY_REPLY_SUBSCRIBED }?.enabled ?: false)
                        updateNotificationOption(NotificationType.FOLLOWING, currentNotificationOptions?.find { it.type == NotificationType.FOLLOWING }?.enabled ?: false)
                        updateNotificationOption(NotificationType.ACTIVITY_MESSAGE, currentNotificationOptions?.find { it.type == NotificationType.ACTIVITY_MESSAGE }?.enabled ?: false)
                        updateNotificationOption(NotificationType.ACTIVITY_MENTION, currentNotificationOptions?.find { it.type == NotificationType.ACTIVITY_MENTION }?.enabled ?: false)
                        updateNotificationOption(NotificationType.ACTIVITY_LIKE, currentNotificationOptions?.find { it.type == NotificationType.ACTIVITY_LIKE }?.enabled ?: false)
                        updateNotificationOption(NotificationType.ACTIVITY_REPLY_LIKE, currentNotificationOptions?.find { it.type == NotificationType.ACTIVITY_REPLY_LIKE }?.enabled ?: false)
                        updateNotificationOption(NotificationType.THREAD_COMMENT_REPLY, currentNotificationOptions?.find { it.type == NotificationType.THREAD_COMMENT_REPLY }?.enabled ?: false)
                        updateNotificationOption(NotificationType.THREAD_COMMENT_MENTION, currentNotificationOptions?.find { it.type == NotificationType.THREAD_COMMENT_MENTION }?.enabled ?: false)
                        updateNotificationOption(NotificationType.THREAD_COMMENT_LIKE, currentNotificationOptions?.find { it.type == NotificationType.THREAD_COMMENT_LIKE }?.enabled ?: false)
                        updateNotificationOption(NotificationType.THREAD_SUBSCRIBED, currentNotificationOptions?.find { it.type == NotificationType.THREAD_SUBSCRIBED }?.enabled ?: false)
                        updateNotificationOption(NotificationType.THREAD_LIKE, currentNotificationOptions?.find { it.type == NotificationType.THREAD_LIKE }?.enabled ?: false)
                    }
            )
        }
    }

    fun saveNotificationsSettings() {
        currentNotificationOptions?.let { currentNotificationOptions ->
            _loading.onNext(true)

            disposables.add(
                userRepository.updateNotificationsSettings(currentNotificationOptions)
                    .applyScheduler()
                    .doFinally { _loading.onNext(false) }
                    .subscribe(
                        {
                            _success.onNext(R.string.settings_saved)
                        },
                        {
                            _error.onNext(it.getStringResource())
                        }
                    )
            )
        }
    }

    fun updateNotificationOption(notificationType: NotificationType, isEnabled: Boolean) {
        val index = currentNotificationOptions?.indexOfFirst { it.type == notificationType }
        if (index != null && index != -1) {
            currentNotificationOptions?.get(index)?.enabled = isEnabled
        }

        val subject = when (notificationType) {
            NotificationType.ACTIVITY_REPLY -> _activityReply
            NotificationType.ACTIVITY_REPLY_SUBSCRIBED -> _activityReplySubscribed
            NotificationType.FOLLOWING -> _following
            NotificationType.ACTIVITY_MESSAGE -> _activityMessage
            NotificationType.ACTIVITY_MENTION -> _activityMention
            NotificationType.ACTIVITY_LIKE -> _activityLike
            NotificationType.ACTIVITY_REPLY_LIKE -> _activityReplyLike
            NotificationType.THREAD_COMMENT_REPLY -> _threadCommentReply
            NotificationType.THREAD_COMMENT_MENTION -> _threadCommentMention
            NotificationType.THREAD_COMMENT_LIKE -> _threadCommentLike
            NotificationType.THREAD_SUBSCRIBED -> _threadSubscribed
            NotificationType.THREAD_LIKE -> _threadLike
            else -> null
        }

        subject?.onNext(isEnabled)
    }
}