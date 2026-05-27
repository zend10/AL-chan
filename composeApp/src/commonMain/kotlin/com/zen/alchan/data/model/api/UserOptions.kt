package com.zen.alchan.data.model.api

import com.zen.alchan.data.enums.UserStaffNameLanguage
import com.zen.alchan.data.enums.UserTitleLanguage
import kotlinx.serialization.Serializable

@Serializable
data class UserOptions(
    val titleLanguage: UserTitleLanguage = UserTitleLanguage.ROMAJI,
    val displayAdultContent: Boolean = false,
    val airingNotifications: Boolean = false,
    val profileColor: String = "",
    val notificationOptions: List<NotificationOption> = listOf(),
    val timezone: String = "",
    val activityMergeTime: Int = 0,
    val staffNameLanguage: UserStaffNameLanguage = UserStaffNameLanguage.ROMAJI,
    val restrictMessagesToFollowing: Boolean = false,
    val disabledListActivity: List<ListActivityOption> = listOf()
)
