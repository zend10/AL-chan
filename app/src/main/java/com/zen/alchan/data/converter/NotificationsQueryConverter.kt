package com.zen.alchan.data.converter

import com.zen.alchan.data.response.NotificationData
import com.zen.alchan.data.response.anilist.*
import fragment.OnAiringNotification
import kotlin.reflect.KClass

fun NotificationsQuery.Data.convert() : NotificationData {
    return NotificationData(
        page = Page(
            pageInfo = PageInfo(
                total = page?.pageInfo?.total ?: 0,
                perPage = page?.pageInfo?.perPage ?: 0,
                currentPage = page?.pageInfo?.currentPage ?: 0,
                lastPage = page?.pageInfo?.lastPage ?: 0,
                hasNextPage = page?.pageInfo?.hasNextPage ?: false
            ),
            data = page?.notifications?.filterNotNull()?.map { notification ->
                when (notification.__typename) {
                    AiringNotification::class.java.simpleName -> {
                        notification.fragments.onAiringNotification?.let {
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
                                        native = it.media?.title?.native_ ?: "",
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
                        notification.fragments.onFollowingNotification?.let {
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
                        notification.fragments.onActivityMessageNotification?.let {
                            ActivityMessageNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
                                context = it.context ?: "",
                                createdAt = it.createdAt ?: 0,
                                message = MessageActivity(
                                    id = it.message?.id ?: 0,
                                    type = it.message?.type,
                                    message = it.message?.message ?: ""
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
                        } ?: ActivityMessageNotification()
                    }
                    ActivityMentionNotification::class.java.simpleName -> {
                        notification.fragments.onActivityMentionNotification?.let {
                            ActivityMentionNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
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
                        } ?: ActivityMentionNotification()
                    }
                    ActivityReplyNotification::class.java.simpleName -> {
                        notification.fragments.onActivityReplyNotification?.let {
                            ActivityReplyNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
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
                        } ?: ActivityReplyNotification()
                    }
                    ActivityReplySubscribedNotification::class.java.simpleName -> {
                        notification.fragments.onActivityReplySubscribedNotification?.let {
                            ActivityReplySubscribedNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
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
                        } ?: ActivityReplySubscribedNotification()
                    }
                    ActivityLikeNotification::class.java.simpleName -> {
                        notification.fragments.onActivityLikeNotification?.let {
                            ActivityLikeNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
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
                        } ?: ActivityLikeNotification()
                    }
                    ActivityReplyLikeNotification::class.java.simpleName -> {
                        notification.fragments.onActivityReplyLikeNotification?.let {
                            ActivityReplyLikeNotification(
                                id = it.id,
                                userId = it.userId,
                                activityId = it.activityId,
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
                        } ?: ActivityReplyLikeNotification()
                    }
                    ThreadCommentMentionNotification::class.java.simpleName -> {
                        notification.fragments.onThreadCommentMentionNotification?.let {
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
                        notification.fragments.onThreadCommentReplyNotification?.let {
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
                        notification.fragments.onThreadCommentSubscribedNotification?.let {
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
                        notification.fragments.onThreadCommentLikeNotification?.let {
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
                        notification.fragments.onThreadLikeNotification?.let {
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
                        notification.fragments.onRelatedMediaAdditionNotification?.let {
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
                                        native = it.media?.title?.native_ ?: "",
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
                        notification.fragments.onMediaDataChangeNotification?.let {
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
                                        native = it.media?.title?.native_ ?: "",
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
                        notification.fragments.onMediaMergeNotification?.let {
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
                                        native = it.media?.title?.native_ ?: "",
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
                        notification.fragments.onMediaDeletionNotification?.let {
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
