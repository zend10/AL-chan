package com.zen.alchan.data.enums

enum class NotificationType {
    ACTIVITY_MESSAGE,
    ACTIVITY_REPLY,
    FOLLOWING,
    ACTIVITY_MENTION,
    THREAD_COMMENT_MENTION,
    THREAD_SUBSCRIBED,
    THREAD_COMMENT_REPLY,
    AIRING,
    ACTIVITY_LIKE,
    ACTIVITY_REPLY_LIKE,
    THREAD_LIKE,
    THREAD_COMMENT_LIKE,
    ACTIVITY_REPLY_SUBSCRIBED,
    RELATED_MEDIA_ADDITION,
    MEDIA_DATA_CHANGE,
    MEDIA_MERGE,
    MEDIA_DELETION,
    MEDIA_SUBMISSION_UPDATE,
    STAFF_SUBMISSION_UPDATE,
    CHARACTER_SUBMISSION_UPDATE
}

fun getNotificationTypeEnum(notificationType: String?): NotificationType? {
    return NotificationType.entries.firstOrNull { it.name.equals(notificationType, true) }
}
