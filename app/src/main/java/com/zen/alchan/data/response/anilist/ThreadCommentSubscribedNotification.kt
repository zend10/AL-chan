package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.type.NotificationType

data class ThreadCommentSubscribedNotification(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: NotificationType = NotificationType.THREAD_SUBSCRIBED,
    val commentId: Int = 0,
    val context: String = "",
    override val createdAt: Int = 0,
    val thread: Thread = Thread(),
    val comment: ThreadComment = ThreadComment(),
    val user: User = User()
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return "${user.name}${context}${thread.title}"
    }
}