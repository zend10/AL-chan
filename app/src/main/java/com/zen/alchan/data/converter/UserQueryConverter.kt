package com.zen.alchan.data.converter

import com.zen.alchan.UserQuery
import com.zen.alchan.data.response.anilist.*

fun UserQuery.Data.convert(): User {
    return User(
        id = User?.id ?: 0,
        name = User?.name ?: "",
        about = User?.about ?: "",
        avatar = UserAvatar(
            large = User?.avatar?.large ?: "",
            medium = User?.avatar?.medium ?: ""
        ),
        bannerImage = User?.bannerImage ?: "",
        isFollowing = User?.isFollowing ?: false,
        isFollower = User?.isFollower ?: false,
        isBlocked = User?.isBlocked ?: false,
        options = UserOptions(
            titleLanguage = User?.options?.titleLanguage,
            displayAdultContent = User?.options?.displayAdultContent ?: false,
            airingNotifications = User?.options?.airingNotifications ?: false,
            notificationOptions = User?.options?.notificationOptions?.map {
                NotificationOption(
                    type = it?.type,
                    enabled = it?.enabled ?: false
                )
            } ?: listOf(),
            timezone = User?.options?.timezone,
            activityMergeTime = User?.options?.activityMergeTime ?: 0,
            staffNameLanguage = User?.options?.staffNameLanguage,
            restrictMessagesToFollowing = User?.options?.restrictMessagesToFollowing ?: false,
            disabledListActivity = User?.options?.disabledListActivity?.map {
                ListActivityOption(
                    disabled = it?.disabled ?: false,
                    type = it?.type
                )
            } ?: listOf()
        ),
        mediaListOptions = MediaListOptions(
            scoreFormat = User?.mediaListOptions?.scoreFormat,
            rowOrder = User?.mediaListOptions?.rowOrder ?: "",
            animeList = MediaListTypeOptions(
                sectionOrder = User?.mediaListOptions?.animeList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = User?.mediaListOptions?.animeList?.splitCompletedSectionByFormat ?: false,
                customLists = User?.mediaListOptions?.animeList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = User?.mediaListOptions?.animeList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = User?.mediaListOptions?.animeList?.advancedScoringEnabled ?: false
            ),
            mangaList = MediaListTypeOptions(
                sectionOrder = User?.mediaListOptions?.mangaList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = User?.mediaListOptions?.mangaList?.splitCompletedSectionByFormat ?: false,
                customLists = User?.mediaListOptions?.mangaList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = User?.mediaListOptions?.mangaList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = User?.mediaListOptions?.mangaList?.advancedScoringEnabled ?: false
            )
        ),
        favourites = Favourites(
            anime = MediaConnection(nodes = User?.favourites?.anime?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            manga = MediaConnection(nodes = User?.favourites?.manga?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            characters = CharacterConnection(nodes = User?.favourites?.characters?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            staff = StaffConnection(nodes = User?.favourites?.staff?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            studios = StudioConnection(nodes = User?.favourites?.studios?.nodes?.mapNotNull { it?.convert() } ?: listOf())
        ),
        statistics = UserStatisticTypes(
            anime = User?.statistics?.anime?.userStatistics?.convert() ?: UserStatistics(),
            manga = User?.statistics?.manga?.userStatistics?.convert() ?: UserStatistics()
        ),
        unreadNotificationCount = User?.unreadNotificationCount ?: 0,
        siteUrl = User?.siteUrl ?: "",
        donatorTier = User?.donatorTier ?: 0,
        donatorBadge = User?.donatorBadge ?: "",
        moderatorRoles = User?.moderatorRoles?.filterNotNull() ?: listOf(),
        createdAt = User?.createdAt ?: 0
    )
}

private fun UserQuery.Node.convert(): Media {
    return Media(
        idAniList = this?.id ?: 0,
        title = MediaTitle(
            romaji = this?.title?.romaji ?: "",
            english = this?.title?.english ?: "",
            native = this?.title?.native ?: "",
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
            native = this?.title?.native ?: "",
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
            native = this?.name?.native ?: "",
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
            native = this?.name?.native ?: "",
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

