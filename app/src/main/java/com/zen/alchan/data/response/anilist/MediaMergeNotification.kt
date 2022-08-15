package com.zen.alchan.data.response.anilist

import type.NotificationType

data class MediaMergeNotification(
    override val id: Int = 0,
    override val type: NotificationType = NotificationType.MEDIA_MERGE,
    val mediaId: Int = 0,
    val deletedMediaTitles: List<String> = listOf(),
    val context: String = "",
    val reason: String = "",
    override val createdAt: Int = 0,
    val media: Media = Media()
) : Notification