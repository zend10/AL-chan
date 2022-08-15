package com.zen.alchan.data.response.anilist

import type.NotificationType

data class MediaDataChangeNotification(
    override val id: Int = 0,
    override val type: NotificationType = NotificationType.MEDIA_DATA_CHANGE,
    val mediaId: Int = 0,
    val context: String = "",
    val reason: String = "",
    override val createdAt: Int = 0,
    val media: Media = Media()
) : Notification