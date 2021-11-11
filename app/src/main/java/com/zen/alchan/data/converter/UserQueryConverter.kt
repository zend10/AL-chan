package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*
import fragment.UserStatistics

fun UserQuery.Data.convert(): User {
    return User(
        id = user?.id ?: 0,
        name = user?.name ?: "",
        about = user?.about ?: "",
        avatar = UserAvatar(
            large = user?.avatar?.large ?: "",
            medium = user?.avatar?.medium ?: ""
        ),
        bannerImage = user?.bannerImage ?: "",
        isFollowing = user?.isFollowing ?: false,
        isFollower = user?.isFollower ?: false,
        isBlocked = user?.isBlocked ?: false,
        options = UserOptions(
            titleLanguage = user?.options?.titleLanguage,
            displayAdultContent = user?.options?.displayAdultContent ?: false,
            airingNotifications = user?.options?.airingNotifications ?: false,
            notificationOptions = user?.options?.notificationOptions?.map {
                NotificationOption(
                    type = it?.type,
                    enabled = it?.enabled ?: false
                )
            } ?: listOf(),
            timezone = user?.options?.timezone,
            activityMergeTime = user?.options?.activityMergeTime ?: 0,
            staffNameLanguage = user?.options?.staffNameLanguage
        ),
        mediaListOptions = MediaListOptions(
            scoreFormat = user?.mediaListOptions?.scoreFormat,
            rowOrder = user?.mediaListOptions?.rowOrder ?: "",
            animeList = MediaListTypeOptions(
                sectionOrder = user?.mediaListOptions?.animeList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = user?.mediaListOptions?.animeList?.splitCompletedSectionByFormat ?: false,
                customLists = user?.mediaListOptions?.animeList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = user?.mediaListOptions?.animeList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = user?.mediaListOptions?.animeList?.advancedScoringEnabled ?: false
            ),
            mangaList = MediaListTypeOptions(
                sectionOrder = user?.mediaListOptions?.mangaList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = user?.mediaListOptions?.mangaList?.splitCompletedSectionByFormat ?: false,
                customLists = user?.mediaListOptions?.mangaList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = user?.mediaListOptions?.mangaList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = user?.mediaListOptions?.mangaList?.advancedScoringEnabled ?: false
            )
        ),
        favourites = Favourites(
            anime = MediaConnection(nodes = user?.favourites?.anime?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            manga = MediaConnection(nodes = user?.favourites?.manga?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            characters = CharacterConnection(nodes = user?.favourites?.characters?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            staff = StaffConnection(nodes = user?.favourites?.staff?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            studios = StudioConnection(nodes = user?.favourites?.studios?.nodes?.mapNotNull { it?.convert() } ?: listOf())
        ),
        statistics = UserStatisticTypes(
            anime = user?.statistics?.anime?.fragments?.userStatistics?.convert() ?: UserStatistics(),
            manga = user?.statistics?.manga?.fragments?.userStatistics?.convert() ?: UserStatistics()
        ),
        unreadNotificationCount = user?.unreadNotificationCount ?: 0,
        siteUrl = user?.siteUrl ?: "",
        donatorTier = user?.donatorTier ?: 0,
        donatorBadge = user?.donatorBadge ?: "",
        moderatorRoles = user?.moderatorRoles?.filterNotNull() ?: listOf()
    )
}

private fun UserQuery.Node.convert(): Media {
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

private fun UserQuery.Node1?.convert(): Media {
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

private fun UserQuery.Node2?.convert(): Character {
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

private fun UserQuery.Node3?.convert(): Staff {
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

private fun UserQuery.Node4?.convert(): Studio {
    return Studio(
        id = this?.id ?: 0,
        name = this?.name ?: "",
        siteUrl = this?.siteUrl ?: ""
    )
}

private fun UserStatistics?.convert(): com.zen.alchan.data.response.anilist.UserStatistics {
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