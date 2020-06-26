package com.zen.alchan.helper.enums

enum class NotificationCategory(val value: String) {
    AIRING_NOTIFICATION("AiringNotification"),
    FOLLOWING_NOTIFICATION("FollowingNotification"),
    ACTIVITY_MESSAGE_NOTIFICATION("ActivityMessageNotification"),
    ACTIVITY_MENTION_NOTIFICATION("ActivityMentionNotification"),
    ACTIVITY_REPLY_NOTIFICATION("ActivityReplyNotification"),
    ACTIVITY_REPLY_SUBSCRIBED_NOTIFICATION("ActivityReplySubscribedNotification"),
    ACTIVITY_LIKE_NOTIFICATION("ActivityLikeNotification"),
    ACTIVITY_REPLY_LIKE_NOTIFICATION("ActivityReplyLikeNotification"),
    THREAD_COMMENT_MENTION_NOTIFICATION("ThreadCommentMentionNotification"),
    THREAD_COMMENT_REPLY_NOTIFICATION("ThreadCommentReplyNotification"),
    THREAD_COMMENT_SUBSCRIBED_NOTIFICATION("ThreadCommentSubscribedNotification"),
    THREAD_COMMENT_LIKE_NOTIFICATION("ThreadCommentLikeNotification"),
    THREAD_LIKE_NOTIFICATION("ThreadLikeNotification"),
    RELATED_MEDIA_ADDITION_NOTIFICATION("RelatedMediaAdditionNotification"),
}