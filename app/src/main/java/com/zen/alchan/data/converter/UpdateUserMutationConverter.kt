package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun UpdateUserMutation.Data.convert(): User {
    return User(
        id = updateUser?.id ?: 0,
        name = updateUser?.name ?: "",
        about = updateUser?.about ?: "",
        avatar = UserAvatar(
            large = updateUser?.avatar?.large ?: "",
            medium = updateUser?.avatar?.medium ?: ""
        ),
        bannerImage = updateUser?.bannerImage ?: "",
        options = UserOptions(
            titleLanguage = updateUser?.options?.titleLanguage,
            displayAdultContent = updateUser?.options?.displayAdultContent ?: false,
            airingNotifications = updateUser?.options?.airingNotifications ?: false,
            notificationOptions = updateUser?.options?.notificationOptions?.map {
                NotificationOption(
                    type = it?.type,
                    enabled = it?.enabled ?: false
                )
            } ?: listOf(),
            timezone = updateUser?.options?.timezone,
            activityMergeTime = updateUser?.options?.activityMergeTime ?: 0
        ),
        mediaListOptions = MediaListOptions(
            scoreFormat = updateUser?.mediaListOptions?.scoreFormat,
            rowOrder = updateUser?.mediaListOptions?.rowOrder ?: "",
            animeList = MediaListTypeOptions(
                sectionOrder = updateUser?.mediaListOptions?.animeList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = updateUser?.mediaListOptions?.animeList?.splitCompletedSectionByFormat ?: false,
                customLists = updateUser?.mediaListOptions?.animeList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = updateUser?.mediaListOptions?.animeList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = updateUser?.mediaListOptions?.animeList?.advancedScoringEnabled ?: false
            ),
            mangaList = MediaListTypeOptions(
                sectionOrder = updateUser?.mediaListOptions?.mangaList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = updateUser?.mediaListOptions?.mangaList?.splitCompletedSectionByFormat ?: false,
                customLists = updateUser?.mediaListOptions?.mangaList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = updateUser?.mediaListOptions?.mangaList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = updateUser?.mediaListOptions?.mangaList?.advancedScoringEnabled ?: false
            )
        ),
        unreadNotificationCount = updateUser?.unreadNotificationCount ?: 0,
        siteUrl = updateUser?.siteUrl ?: "",
        donatorTier = updateUser?.donatorTier ?: 0,
        donatorBadge = updateUser?.donatorBadge ?: "",
        moderatorRoles = updateUser?.moderatorRoles?.filterNotNull() ?: listOf()
    )
}