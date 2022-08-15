package com.zen.alchan.data.response.anilist

import type.NotificationType

data class AiringNotification(
    override val id: Int = 0,
    override val type: NotificationType = NotificationType.AIRING,
    val animeId: Int = 0,
    val episode: Int = 0,
    val contexts: List<String> = listOf(),
    override val createdAt: Int = 0,
    val media: Media = Media()
) : Notification