package com.zen.alchan.data.response

import type.UserTitleLanguage

class UserOptions(
    val titleLanguage: UserTitleLanguage?,
    val displayAdultContent: Boolean?,
    val airingNotifications: Boolean?,
    val notificationOptions: List<NotificationOption>?
)