package com.zen.alchan.data.converter

import com.zen.alchan.ViewerQuery
import com.zen.alchan.data.response.anilist.*

fun ViewerQuery.Data.convert(): User {
    return User(
        id = Viewer?.id ?: 0,
        name = Viewer?.name ?: "",
        about = Viewer?.about ?: "",
        avatar = UserAvatar(
            large = Viewer?.avatar?.large ?: "",
            medium = Viewer?.avatar?.medium ?: ""
        ),
        bannerImage = Viewer?.bannerImage ?: "",
        isFollowing = Viewer?.isFollowing ?: false,
        isFollower = Viewer?.isFollower ?: false,
        isBlocked = Viewer?.isBlocked ?: false,
        options = UserOptions(
            titleLanguage = Viewer?.options?.titleLanguage,
            displayAdultContent = Viewer?.options?.displayAdultContent ?: false,
            airingNotifications = Viewer?.options?.airingNotifications ?: false,
            notificationOptions = Viewer?.options?.notificationOptions?.map {
                NotificationOption(
                    type = it?.type,
                    enabled = it?.enabled ?: false
                )
            } ?: listOf(),
            timezone = Viewer?.options?.timezone,
            activityMergeTime = Viewer?.options?.activityMergeTime ?: 0,
            staffNameLanguage = Viewer?.options?.staffNameLanguage,
            restrictMessagesToFollowing = Viewer?.options?.restrictMessagesToFollowing ?: false,
            disabledListActivity = Viewer?.options?.disabledListActivity?.map {
                ListActivityOption(
                    disabled = it?.disabled ?: false,
                    type = it?.type
                )
            } ?: listOf()
        ),
        mediaListOptions = MediaListOptions(
            scoreFormat = Viewer?.mediaListOptions?.scoreFormat,
            rowOrder = Viewer?.mediaListOptions?.rowOrder ?: "",
            animeList = MediaListTypeOptions(
                sectionOrder = Viewer?.mediaListOptions?.animeList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = Viewer?.mediaListOptions?.animeList?.splitCompletedSectionByFormat ?: false,
                customLists = Viewer?.mediaListOptions?.animeList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = Viewer?.mediaListOptions?.animeList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = Viewer?.mediaListOptions?.animeList?.advancedScoringEnabled ?: false
            ),
            mangaList = MediaListTypeOptions(
                sectionOrder = Viewer?.mediaListOptions?.mangaList?.sectionOrder?.filterNotNull() ?: listOf(),
                splitCompletedSectionByFormat = Viewer?.mediaListOptions?.mangaList?.splitCompletedSectionByFormat ?: false,
                customLists = Viewer?.mediaListOptions?.mangaList?.customLists?.filterNotNull() ?: listOf(),
                advancedScoring = Viewer?.mediaListOptions?.mangaList?.advancedScoring?.filterNotNull() ?: listOf(),
                advancedScoringEnabled = Viewer?.mediaListOptions?.mangaList?.advancedScoringEnabled ?: false
            )
        ),
        favourites = Favourites(
            anime = MediaConnection(nodes = Viewer?.favourites?.anime?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            manga = MediaConnection(nodes = Viewer?.favourites?.manga?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            characters = CharacterConnection(nodes = Viewer?.favourites?.characters?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            staff = StaffConnection(nodes = Viewer?.favourites?.staff?.nodes?.mapNotNull { it?.convert() } ?: listOf()),
            studios = StudioConnection(nodes = Viewer?.favourites?.studios?.nodes?.mapNotNull { it?.convert() } ?: listOf())
        ),
        statistics = UserStatisticTypes(
            anime = Viewer?.statistics?.anime?.userStatistics?.convert() ?: UserStatistics(),
            manga = Viewer?.statistics?.manga?.userStatistics?.convert() ?: UserStatistics()
        ),
        unreadNotificationCount = Viewer?.unreadNotificationCount ?: 0,
        siteUrl = Viewer?.siteUrl ?: "",
        donatorTier = Viewer?.donatorTier ?: 0,
        donatorBadge = Viewer?.donatorBadge ?: "",
        moderatorRoles = Viewer?.moderatorRoles?.filterNotNull() ?: listOf(),
        createdAt = Viewer?.createdAt ?: 0
    )
}

private fun ViewerQuery.Node.convert(): Media {
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

private fun ViewerQuery.Node1?.convert(): Media {
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

private fun ViewerQuery.Node2?.convert(): Character {
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

private fun ViewerQuery.Node3?.convert(): Staff {
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

private fun ViewerQuery.Node4?.convert(): Studio {
    return Studio(
        id = this?.id ?: 0,
        name = this?.name ?: "",
        siteUrl = this?.siteUrl ?: ""
    )
}