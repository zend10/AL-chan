package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*
import fragment.ViewerStatistics

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
        isFollowing = viewer?.isFollowing ?: false,
        isFollower = viewer?.isFollower ?: false,
        isBlocked = viewer?.isBlocked ?: false,
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
            activityMergeTime = viewer?.options?.activityMergeTime ?: 0,
            staffNameLanguage = viewer?.options?.staffNameLanguage
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
        favourites = Favourites(
            anime = MediaConnection(nodes = viewer?.favourites?.anime?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            manga = MediaConnection(nodes = viewer?.favourites?.manga?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            characters = CharacterConnection(nodes = viewer?.favourites?.characters?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            staff = StaffConnection(nodes = viewer?.favourites?.staff?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            studios = StudioConnection(nodes = viewer?.favourites?.studios?.nodes?.mapNotNull { it?.convert() } ?: listOf())
        ),
        statistics = UserStatisticTypes(
            anime = viewer?.statistics?.anime?.fragments?.viewerStatistics?.convert() ?: UserStatistics(),
            manga = viewer?.statistics?.manga?.fragments?.viewerStatistics?.convert() ?: UserStatistics()
        ),
        unreadNotificationCount = viewer?.unreadNotificationCount ?: 0,
        siteUrl = viewer?.siteUrl ?: "",
        donatorTier = viewer?.donatorTier ?: 0,
        donatorBadge = viewer?.donatorBadge ?: "",
        moderatorRoles = viewer?.moderatorRoles?.filterNotNull() ?: listOf()
    )
}

private fun ViewerQuery.Node.convert(): Media {
    return Media(
        idAniList = this?.id ?: 0,
        title = MediaTitle(
            romaji = this?.title?.romaji ?: "",
            english = this?.title?.english ?: "",
            native = this?.title?.native_ ?: "",
            userPreferred = this?.title?.userPreferred ?: "",
        ),
        coverImage = MediaCoverImage(
            extraLarge = this?.coverImage?.extraLarge ?: "",
            large = this?.coverImage?.large ?: "",
            medium = this?.coverImage?.medium ?: "",
        ),
        siteUrl = this?.siteUrl ?: ""
    )
}

private fun ViewerQuery.Node1?.convert(): Media {
    return Media(
        idAniList = this?.id ?: 0,
        title = MediaTitle(
            romaji = this?.title?.romaji ?: "",
            english = this?.title?.english ?: "",
            native = this?.title?.native_ ?: "",
            userPreferred = this?.title?.userPreferred ?: "",
        ),
        coverImage = MediaCoverImage(
            extraLarge = this?.coverImage?.extraLarge ?: "",
            large = this?.coverImage?.large ?: "",
            medium = this?.coverImage?.medium ?: "",
        ),
        siteUrl = this?.siteUrl ?: ""
    )
}

private fun ViewerQuery.Node2?.convert(): Character {
    return Character(
        id = this?.id ?: 0,
        name = CharacterName(
            first = this?.name?.first ?: "",
            middle = this?.name?.middle ?: "",
            last = this?.name?.last ?: "",
            full = this?.name?.full ?: "",
            native = this?.name?.native_ ?: "",
            alternative = this?.name?.alternative?.filterNotNull() ?: listOf(),
            alternativeSpoiler = this?.name?.alternativeSpoiler?.filterNotNull() ?: listOf(),
            userPreferred = this?.name?.userPreferred ?: "",
        ),
        image = CharacterImage(
            large = this?.image?.large ?: "",
            medium = this?.image?.medium ?: ""
        ),
        siteUrl = this?.siteUrl ?: ""
    )
}

private fun ViewerQuery.Node3?.convert(): Staff {
    return Staff(
        id = this?.id ?: 0,
        name = StaffName(
            first = this?.name?.first ?: "",
            middle = this?.name?.middle ?: "",
            last = this?.name?.last ?: "",
            full = this?.name?.full ?: "",
            native = this?.name?.native_ ?: "",
            alternative = this?.name?.alternative?.filterNotNull() ?: listOf(),
            userPreferred = this?.name?.userPreferred ?: "",
        ),
        language = this?.languageV2 ?: "",
        image = StaffImage(
            large = this?.image?.large ?: "",
            medium = this?.image?.medium ?: ""
        ),
        siteUrl = this?.siteUrl ?: ""
    )
}

private fun ViewerQuery.Node4?.convert(): Studio {
    return Studio(
        id = this?.id ?: 0,
        name = this?.name ?: "",
        siteUrl = this?.siteUrl ?: ""
    )
}

private fun ViewerStatistics?.convert(): UserStatistics {
    return UserStatistics(
        count = this?.count ?: 0,
        meanScore = this?.meanScore ?: 0.0,
        standardDeviation = this?.standardDeviation ?: 0.0,
        minutesWatched = this?.minutesWatched ?: 0,
        episodesWatched = this?.episodesWatched ?: 0,
        chaptersRead = this?.chaptersRead ?: 0,
        volumesRead = this?.volumesRead ?: 0,
        statuses = this?.statuses?.mapNotNull {
            UserStatusStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                status = it?.status
            )
        } ?: listOf(),
        scores = this?.scores?.mapNotNull {
            UserScoreStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                score = it?.score ?: 0
            )
        } ?: listOf(),
        releaseYears = this?.releaseYears?.mapNotNull {
            UserReleaseYearStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                releaseYear = it?.releaseYear ?: 0
            )
        } ?: listOf(),
        startYears = this?.startYears?.mapNotNull {
            UserStartYearStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                startYear = it?.startYear ?: 0
            )
        } ?: listOf(),
        genres = this?.genres?.mapNotNull {
            UserGenreStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                genre = it?.genre ?: ""
            )
        } ?: listOf(),
        tags = this?.tags?.mapNotNull {
            UserTagStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                tag = MediaTag(
                    id = it?.tag?.id ?: 0,
                    name = it?.tag?.name ?: ""
                )
            )
        } ?: listOf(),
        studios = this?.studios?.mapNotNull {
            UserStudioStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                studio = Studio(
                    id = it?.studio?.id ?: 0,
                    name = it?.studio?.name ?: "",
                    isAnimationStudio = it?.studio?.isAnimationStudio ?: false
                )
            )
        } ?: listOf(),
    )
}