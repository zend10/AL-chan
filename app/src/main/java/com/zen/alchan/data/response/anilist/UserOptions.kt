package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.response.anilist.NotificationOption
import type.UserTitleLanguage

data class UserOptions(
    var titleLanguage: UserTitleLanguage? = null,
    var displayAdultContent: Boolean = false,
    var airingNotifications: Boolean = false,
    val notificationOptions: List<NotificationOption> = listOf(),
    val timezone: String? = null,
    var activityMergeTime: Int = 0
)