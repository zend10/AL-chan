package com.zen.alchan.data.response

import type.UserTitleLanguage

data class UserOptions(
    val titleLanguage: UserTitleLanguage? = null,
    val displayAdultContent: Boolean = false,
    val airingNotifications: Boolean = false,
    val notificationOptions: List<NotificationOption> = listOf(),
    val timezone: String? = null,
    val activityMergeTime: Int = 0
)