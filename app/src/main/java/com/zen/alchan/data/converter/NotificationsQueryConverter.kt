package com.zen.alchan.data.converter

import com.zen.alchan.NotificationsQuery
import com.zen.alchan.data.response.NotificationData
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.fragment.OnNotificationListActivity
import com.zen.alchan.fragment.OnNotificationMessageActivity
import com.zen.alchan.fragment.OnNotificationTextActivity

fun NotificationsQuery.Data.convert() : NotificationData {
    return NotificationData(
        page = Page(
            pageInfo = PageInfo(
                total = Page?.pageInfo?.total ?: 0,
                perPage = Page?.pageInfo?.perPage ?: 0,
                currentPage = Page?.pageInfo?.currentPage ?: 0,
                lastPage = Page?.pageInfo?.lastPage ?: 0,
                hasNextPage = Page?.pageInfo?.hasNextPage ?: false
            ),
            data = Page?.notifications?.filterNotNull()?.map { notification ->
                when (notification.__typename) {
                    AiringNotification::class.java.simpleName -> {
                        notification.onAiringNotification?.let {
                            AiringNotification(
                                id = it.id,
                                animeId = it.animeId,
                                episode = it.episode,
                                contexts = it.contexts?.filterNotNull() ?: listOf(),
                                createdAt = it.createdAt ?: 0,
                                media = Media(
                                    idAniList = it.media?.id ?: 0,
                                    title = MediaTitle(
                                        romaji = it.media?.title?.romaji ?: "",
                                        english = it.media?.title?.english ?: "",
                                        native = it.media?.title?.native ?: "",
                                        userPreferred = it.media?.title?.userPreferred ?: ""
                                    ),
                                    coverImage = MediaCoverImage(
                                        extraLarge = it.media?.coverImage?.extraLarge ?: "",
                                        large = it.media?.coverImage?.large ?: "",
                                        medium = it.media?.coverImage?.medium ?: ""
                                    ),
                                    type = it.media?.type
                                )
                            )
                        } ?: AiringNotification()
                    }
                    FollowingNotification::class.java.simpleName -> {
                        notification.onFollowingNotification?.let {
                            FollowingNotification(
                                id = it.id,
                                userId = it.userId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: FollowingNotification()
                    }
                    ActivityMessageNotification::class.java.simpleName -> {
                        notification.onActivityMessageNotification?.let {
                            ActivityMessageNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                message = it.message?.onNotificationMessageActivity?.convert(),
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ActivityMessageNotification()
                    }
                    ActivityMentionNotification::class.java.simpleName -> {
                        notification.onActivityMentionNotification?.let {
                            ActivityMentionNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                activity = when (it.activity?.__typename) {
                                    TextActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationTextActivity?.convert()
                                    }
                                    ListActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationListActivity?.convert()
                                    }
                                    MessageActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationMessageActivity?.convert()
                                    }
                                    else -> null
                                },
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ActivityMentionNotification()
                    }
                    ActivityReplyNotification::class.java.simpleName -> {
                        notification.onActivityReplyNotification?.let {
                            ActivityReplyNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                activity = when (it.activity?.__typename) {
                                    TextActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationTextActivity?.convert()
                                    }
                                    ListActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationListActivity?.convert()
                                    }
                                    MessageActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationMessageActivity?.convert()
                                    }
                                    else -> null
                                },
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ActivityReplyNotification()
                    }
                    ActivityReplySubscribedNotification::class.java.simpleName -> {
                        notification.onActivityReplySubscribedNotification?.let {
                            ActivityReplySubscribedNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                activity = when (it.activity?.__typename) {
                                    TextActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationTextActivity?.convert()
                                    }
                                    ListActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationListActivity?.convert()
                                    }
                                    MessageActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationMessageActivity?.convert()
                                    }
                                    else -> null
                                },
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ActivityReplySubscribedNotification()
                    }
                    ActivityLikeNotification::class.java.simpleName -> {
                        notification.onActivityLikeNotification?.let {
                            ActivityLikeNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                activity = when (it.activity?.__typename) {
                                    TextActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationTextActivity?.convert()
                                    }
                                    ListActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationListActivity?.convert()
                                    }
                                    MessageActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationMessageActivity?.convert()
                                    }
                                    else -> null
                                },
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ActivityLikeNotification()
                    }
                    ActivityReplyLikeNotification::class.java.simpleName -> {
                        notification.onActivityReplyLikeNotification?.let {
                            ActivityReplyLikeNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                activity = when (it.activity?.__typename) {
                                    TextActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationTextActivity?.convert()
                                    }
                                    ListActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationListActivity?.convert()
                                    }
                                    MessageActivity::class.java.simpleName -> {
                                        it.activity?.onNotificationMessageActivity?.convert()
                                    }
                                    else -> null
                                },
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ActivityReplyLikeNotification()
                    }
                    ThreadCommentMentionNotification::class.java.simpleName -> {
                        notification.onThreadCommentMentionNotification?.let {
                            ThreadCommentMentionNotification(
                                id = it.id,
                                userId = it.userId,
                                commentId = it.commentId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                thread = Thread(
                                    id = it.thread?.id ?: 0,
                                    title = it.thread?.title ?: "",
                                    siteUrl = it.thread?.siteUrl ?: ""
                                ),
                                comment = ThreadComment(
                                    id = it.comment?.id ?: 0,
                                    threadId = it.comment?.threadId ?: 0,
                                    comment = it.comment?.comment ?: "",
                                    siteUrl = it.comment?.siteUrl ?: ""
                                ),
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ThreadCommentMentionNotification()
                    }
                    ThreadCommentReplyNotification::class.java.simpleName -> {
                        notification.onThreadCommentReplyNotification?.let {
                            ThreadCommentReplyNotification(
                                id = it.id,
                                userId = it.userId,
                                commentId = it.commentId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                thread = Thread(
                                    id = it.thread?.id ?: 0,
                                    title = it.thread?.title ?: "",
                                    siteUrl = it.thread?.siteUrl ?: ""
                                ),
                                comment = ThreadComment(
                                    id = it.comment?.id ?: 0,
                                    threadId = it.comment?.threadId ?: 0,
                                    comment = it.comment?.comment ?: "",
                                    siteUrl = it.comment?.siteUrl ?: ""
                                ),
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ThreadCommentReplyNotification()
                    }
                    ThreadCommentSubscribedNotification::class.java.simpleName -> {
                        notification.onThreadCommentSubscribedNotification?.let {
                            ThreadCommentSubscribedNotification(
                                id = it.id,
                                userId = it.userId,
                                commentId = it.commentId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                thread = Thread(
                                    id = it.thread?.id ?: 0,
                                    title = it.thread?.title ?: "",
                                    siteUrl = it.thread?.siteUrl ?: ""
                                ),
                                comment = ThreadComment(
                                    id = it.comment?.id ?: 0,
                                    threadId = it.comment?.threadId ?: 0,
                                    comment = it.comment?.comment ?: "",
                                    siteUrl = it.comment?.siteUrl ?: ""
                                ),
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ThreadCommentSubscribedNotification()
                    }
                    ThreadCommentLikeNotification::class.java.simpleName -> {
                        notification.onThreadCommentLikeNotification?.let {
                            ThreadCommentLikeNotification(
                                id = it.id,
                                userId = it.userId,
                                commentId = it.commentId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                thread = Thread(
                                    id = it.thread?.id ?: 0,
                                    title = it.thread?.title ?: "",
                                    siteUrl = it.thread?.siteUrl ?: ""
                                ),
                                comment = ThreadComment(
                                    id = it.comment?.id ?: 0,
                                    threadId = it.comment?.threadId ?: 0,
                                    comment = it.comment?.comment ?: "",
                                    siteUrl = it.comment?.siteUrl ?: ""
                                ),
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ThreadCommentLikeNotification()
                    }
                    ThreadLikeNotification::class.java.simpleName -> {
                        notification.onThreadLikeNotification?.let {
                            ThreadLikeNotification(
                                id = it.id,
                                userId = it.userId,
                                threadId = it.threadId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                thread = Thread(
                                    id = it.thread?.id ?: 0,
                                    title = it.thread?.title ?: "",
                                    siteUrl = it.thread?.siteUrl ?: ""
                                ),
                                comment = ThreadComment(
                                    id = it.comment?.id ?: 0,
                                    threadId = it.comment?.threadId ?: 0,
                                    comment = it.comment?.comment ?: "",
                                    siteUrl = it.comment?.siteUrl ?: ""
                                ),
                                user = User(
                                    id = it.user?.id ?: 0,
                                    name = it.user?.name ?: "",
                                    avatar = UserAvatar(
                                        large = it.user?.avatar?.large ?: "",
                                        medium = it.user?.avatar?.medium ?: ""
                                    )
                                )
                            )
                        } ?: ThreadLikeNotification()
                    }
                    RelatedMediaAdditionNotification::class.java.simpleName -> {
                        notification.onRelatedMediaAdditionNotification?.let {
                            RelatedMediaAdditionNotification(
                                id = it.id,
                                mediaId = it.mediaId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                media = Media(
                                    idAniList = it.media?.id ?: 0,
                                    title = MediaTitle(
                                        romaji = it.media?.title?.romaji ?: "",
                                        english = it.media?.title?.english ?: "",
                                        native = it.media?.title?.native ?: "",
                                        userPreferred = it.media?.title?.userPreferred ?: ""
                                    ),
                                    coverImage = MediaCoverImage(
                                        extraLarge = it.media?.coverImage?.extraLarge ?: "",
                                        large = it.media?.coverImage?.large ?: "",
                                        medium = it.media?.coverImage?.medium ?: ""
                                    ),
                                    type = it.media?.type
                                )
                            )
                        } ?: RelatedMediaAdditionNotification()
                    }
                    MediaDataChangeNotification::class.java.simpleName -> {
                        notification.onMediaDataChangeNotification?.let {
                            MediaDataChangeNotification(
                                id = it.id,
                                mediaId = it.mediaId,
                                context = it.context ?: "",
                                reason = it.reason ?: "",
                                createdAt = it.createdAt ?: 0,
                                media = Media(
                                    idAniList = it.media?.id ?: 0,
                                    title = MediaTitle(
                                        romaji = it.media?.title?.romaji ?: "",
                                        english = it.media?.title?.english ?: "",
                                        native = it.media?.title?.native ?: "",
                                        userPreferred = it.media?.title?.userPreferred ?: ""
                                    ),
                                    coverImage = MediaCoverImage(
                                        extraLarge = it.media?.coverImage?.extraLarge ?: "",
                                        large = it.media?.coverImage?.large ?: "",
                                        medium = it.media?.coverImage?.medium ?: ""
                                    ),
                                    type = it.media?.type
                                )
                            )
                        } ?: MediaDataChangeNotification()
                    }
                    MediaMergeNotification::class.java.simpleName -> {
                        notification.onMediaMergeNotification?.let {
                            MediaMergeNotification(
                                id = it.id,
                                mediaId = it.mediaId,
                                deletedMediaTitles = it.deletedMediaTitles?.filterNotNull() ?: listOf(),
                                context = it.context ?: "",
                                reason = it.reason ?: "",
                                createdAt = it.createdAt ?: 0,
                                media = Media(
                                    idAniList = it.media?.id ?: 0,
                                    title = MediaTitle(
                                        romaji = it.media?.title?.romaji ?: "",
                                        english = it.media?.title?.english ?: "",
                                        native = it.media?.title?.native ?: "",
                                        userPreferred = it.media?.title?.userPreferred ?: ""
                                    ),
                                    coverImage = MediaCoverImage(
                                        extraLarge = it.media?.coverImage?.extraLarge ?: "",
                                        large = it.media?.coverImage?.large ?: "",
                                        medium = it.media?.coverImage?.medium ?: ""
                                    ),
                                    type = it.media?.type
                                )
                            )
                        } ?: MediaMergeNotification()
                    }
                    MediaDeletionNotification::class.java.simpleName -> {
                        notification.onMediaDeletionNotification?.let {
                            MediaDeletionNotification(
                                id = it.id,
                                deletedMediaTitle = it.deletedMediaTitle ?: "",
                                context = it.context ?: "",
                                reason = it.reason ?: "",
                                createdAt = it.createdAt ?: 0
                            )
                        } ?: MediaDeletionNotification()
                    }
                    else -> AiringNotification()
                }
            } ?: listOf()
        )
    )
}

private fun OnNotificationTextActivity.convert(): TextActivity {
    return TextActivity(
        id = id,
        text = text ?: "",
        siteUrl = siteUrl ?: "",
    )
}

private fun OnNotificationListActivity.convert() : ListActivity {
    return ListActivity(
        id = id,
        status = status ?: "",
        progress = progress ?: "",
        siteUrl = siteUrl ?: "",
        media = media?.let {
            Media(
                idAniList = it.id,
                title = MediaTitle(
                    romaji = it.title?.romaji ?: "",
                    english = it.title?.english ?: "",
                    native = it.title?.native ?: "",
                    userPreferred = it.title?.userPreferred ?: ""
                )
            )
        } ?: Media(),
    )
}

private fun OnNotificationMessageActivity.convert() : MessageActivity {
    return MessageActivity(
        id = id,
        message = message ?: "",
        siteUrl = siteUrl ?: "",
    )
}