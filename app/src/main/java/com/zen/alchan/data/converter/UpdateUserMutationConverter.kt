package com.zen.alchan.data.converter

import com.zen.alchan.UpdateUserMutation
import com.zen.alchan.data.response.anilist.*

fun UpdateUserMutation.Data.convert(): User {
    return User(
        id = UpdateUser?.id ?: 0,
        name = UpdateUser?.name ?: "",
        about = UpdateUser?.about ?: "",
        avatar = UserAvatar(
            large = UpdateUser?.avatar?.large ?: "",
            medium = UpdateUser?.avatar?.medium ?: ""
        ),
        bannerImage = UpdateUser?.bannerImage ?: "",
        options = UserOptions(
            titleLanguage = UpdateUser?.options?.titleLanguage,
            displayAdultContent = UpdateUser?.options?.displayAdultContent ?: false,
            airingNotifications = UpdateUser?.options?.airingNotifications ?: false,
            notificationOptions = UpdateUser?.options?.notificationOptions?.map {
                NotificationOption(
                    type = it?.type,
                    enabled = it?.enabled ?: false
                )
            } ?: listOf(),
            timezone = UpdateUser?.options?.timezone,
            activityMergeTime = UpdateUser?.options?.activityMergeTime ?: 0,
            staffNameLanguage = UpdateUser?.options?.staffNameLanguage,
            restrictMessagesToFollowing = UpdateUser?.options?.restrictMessagesToFollowing ?: false,
            disabledListActivity = UpdateUser?.options?.disabledListActivity?.map {
                ListActivityOption(
                    disabled = it?.disabled ?: false,
                    type = it?.type
                )
            } ?: listOf()
        ),
        mediaListOptions = MediaListOptions(
            scoreFormat = UpdateUser?.mediaListOptions?.scoreFormat,
            rowOrder = UpdateUser?.mediaListOptions?.rowOrder ?: "",
            animeList = MediaListTypeOptions(
                sectionOrder = UpdateUser?.mediaListOptions?.animeList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = UpdateUser?.mediaListOptions?.animeList?.splitCompletedSectionByFormat ?: false,
                customLists = UpdateUser?.mediaListOptions?.animeList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = UpdateUser?.mediaListOptions?.animeList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = UpdateUser?.mediaListOptions?.animeList?.advancedScoringEnabled ?: false
            ),
            mangaList = MediaListTypeOptions(
                sectionOrder = UpdateUser?.mediaListOptions?.mangaList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = UpdateUser?.mediaListOptions?.mangaList?.splitCompletedSectionByFormat ?: false,
                customLists = UpdateUser?.mediaListOptions?.mangaList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = UpdateUser?.mediaListOptions?.mangaList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = UpdateUser?.mediaListOptions?.mangaList?.advancedScoringEnabled ?: false
            )
        ),
        unreadNotificationCount = UpdateUser?.unreadNotificationCount ?: 0,
        siteUrl = UpdateUser?.siteUrl ?: "",
        donatorTier = UpdateUser?.donatorTier ?: 0,
        donatorBadge = UpdateUser?.donatorBadge ?: "",
        moderatorRoles = UpdateUser?.moderatorRoles?.filterNotNull() ?: listOf(),
        createdAt = UpdateUser?.createdAt ?: 0
    )
}