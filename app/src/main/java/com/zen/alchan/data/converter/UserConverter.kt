package com.zen.alchan.data.converter

import com.zen.alchan.data.response.*

fun ViewerQuery.Data.convert(): User {
    return User(
        id = viewer?.id ?: 0,
        name = viewer?.name ?: "",
        about = viewer?.about ?: "",
        avatar = UserAvatar(
            large = viewer?.avatar?.large ?: "",
            medium = viewer?.avatar?.medium ?: ""
        ),
        bannerImage = viewer?.bannerImage ?: "",
        options = UserOptions(
            titleLanguage = viewer?.options?.titleLanguage,
            displayAdultContent = viewer?.options?.displayAdultContent ?: false,
            airingNotifications = viewer?.options?.airingNotifications ?: false,
            notificationOptions = viewer?.options?.notificationOptions?.map {
                NotificationOption(
                    type = it?.type,
                    enabled = it?.enabled ?: false
                )
            } ?: listOf(),
            timezone = viewer?.options?.timezone,
            activityMergeTime = viewer?.options?.activityMergeTime ?: 0
        ),
        mediaListOptions = MediaListOptions(
            scoreFormat = viewer?.mediaListOptions?.scoreFormat,
            rowOrder = viewer?.mediaListOptions?.rowOrder ?: "",
            animeList = MediaListTypeOptions(
                sectionOrder = viewer?.mediaListOptions?.animeList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = viewer?.mediaListOptions?.animeList?.splitCompletedSectionByFormat ?: false,
                customLists = viewer?.mediaListOptions?.animeList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = viewer?.mediaListOptions?.animeList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = viewer?.mediaListOptions?.animeList?.advancedScoringEnabled ?: false
            ),
            mangaList = MediaListTypeOptions(
                sectionOrder = viewer?.mediaListOptions?.mangaList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = viewer?.mediaListOptions?.mangaList?.splitCompletedSectionByFormat ?: false,
                customLists = viewer?.mediaListOptions?.mangaList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = viewer?.mediaListOptions?.mangaList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = viewer?.mediaListOptions?.mangaList?.advancedScoringEnabled ?: false
            )
        ),
        statistics = UserStatisticTypes(
            anime = UserStatistics(
                count = viewer?.statistics?.anime?.count ?: 0,
                meanScore = viewer?.statistics?.anime?.meanScore ?: 0.0,
                standardDeviation = viewer?.statistics?.anime?.standardDeviation ?: 0.0,
                minutesWatched = viewer?.statistics?.anime?.minutesWatched ?: 0,
                episodesWatched = viewer?.statistics?.anime?.episodesWatched ?: 0,
                chaptersRead = viewer?.statistics?.anime?.chaptersRead ?: 0,
                volumesRead = viewer?.statistics?.anime?.volumesRead ?: 0,
            ),
            manga = UserStatistics(
                count = viewer?.statistics?.anime?.count ?: 0,
                meanScore = viewer?.statistics?.anime?.meanScore ?: 0.0,
                standardDeviation = viewer?.statistics?.anime?.standardDeviation ?: 0.0,
                minutesWatched = viewer?.statistics?.anime?.minutesWatched ?: 0,
                episodesWatched = viewer?.statistics?.anime?.episodesWatched ?: 0,
                chaptersRead = viewer?.statistics?.anime?.chaptersRead ?: 0,
                volumesRead = viewer?.statistics?.anime?.volumesRead ?: 0,
            )
        ),
        unreadNotificationCount = viewer?.unreadNotificationCount ?: 0,
        siteUrl = viewer?.siteUrl ?: "",
        donatorTier = viewer?.donatorTier ?: 0,
        donatorBadge = viewer?.donatorBadge ?: "",
        moderatorStatus = viewer?.moderatorStatus ?: ""
    )
}