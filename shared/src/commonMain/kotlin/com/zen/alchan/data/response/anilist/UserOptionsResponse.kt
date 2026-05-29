package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.enums.UserStaffNameLanguage
import com.zen.alchan.data.enums.UserTitleLanguage
import com.zen.alchan.data.enums.getUserStaffNameLanguageEnum
import com.zen.alchan.data.enums.getUserTitleLanguageEnum
import com.zen.alchan.data.model.api.UserOptions
import kotlinx.serialization.Serializable

@Serializable
data class UserOptionsResponse(
    val titleLanguage: String? = null,
    val displayAdultContent: Boolean? = null,
    val airingNotifications: Boolean? = null,
    val profileColor: String? = null,
    val notificationOptions: List<NotificationOptionResponse>? = null,
    val timezone: String? = null,
    val activityMergeTime: Int? = null,
    val staffNameLanguage: String? = null,
    val restrictMessagesToFollowing: Boolean? = null,
    val disabledListActivity: List<ListActivityOptionResponse>? = null
) {
    fun toModel(): UserOptions {
        return UserOptions(
            titleLanguage = getUserTitleLanguageEnum(titleLanguage) ?: UserTitleLanguage.ROMAJI,
            displayAdultContent = displayAdultContent ?: false,
            airingNotifications = airingNotifications ?: false,
            profileColor = profileColor ?: "",
            notificationOptions = notificationOptions?.map { it.toModel() } ?: listOf(),
            timezone = timezone ?: "",
            activityMergeTime = activityMergeTime ?: 0,
            staffNameLanguage = getUserStaffNameLanguageEnum(staffNameLanguage)
                ?: UserStaffNameLanguage.ROMAJI,
            restrictMessagesToFollowing = restrictMessagesToFollowing ?: false,
            disabledListActivity = disabledListActivity?.map { it.toModel() } ?: listOf()
        )
    }
}
