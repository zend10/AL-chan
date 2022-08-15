package com.zen.alchan.data.response.anilist

import type.NotificationType

interface Notification {
    val id: Int
    val type: NotificationType
    val createdAt: Int
}