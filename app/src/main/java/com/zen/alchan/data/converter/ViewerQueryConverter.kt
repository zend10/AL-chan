package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

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
            anime = viewer?.statistics?.anime?.fragments?.userStatistics?.convert() ?: UserStatistics(),
            manga = viewer?.statistics?.manga?.fragments?.userStatistics?.convert() ?: UserStatistics()
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