package com.zen.alchan.data.network

import com.zen.alchan.data.response.*

object Converter {

    fun convertUser(viewer: ViewerQuery.Viewer?): User {
        return User(
            id = viewer?.id()!!,
            name = viewer.name(),
            about = viewer.about(),
            avatar = convertUserAvatar(viewer.avatar()),
            bannerImage = viewer.bannerImage(),
            options = convertUserOptions(viewer.options()),
            mediaListOptions = convertMediaListOptions(viewer.mediaListOptions()),
            statistics = convertUserStatisticTypes(viewer.statistics()),
            unreadNotificationCount = viewer.unreadNotificationCount(),
            siteUrl = viewer.siteUrl()
        )
    }

    fun convertUserAvatar(avatar: ViewerQuery.Avatar?): UserAvatar {
        return UserAvatar(
            large = avatar?.large(),
            medium = avatar?.medium()
        )
    }

    fun convertNotificationOptionList(notificationOption: List<ViewerQuery.NotificationOption>?): List<NotificationOption> {
        val notificationOptionList = ArrayList<NotificationOption>()
        notificationOption?.forEach {
            notificationOptionList.add(
                NotificationOption(
                    type = it.type(),
                    enabled = it.enabled()
                )
            )
        }
        return notificationOptionList
    }

    // Thanks JVM
    fun convertNotificationOptionListFromSettingsMutation(notificationOption: List<AniListSettingsMutation.NotificationOption>?): List<NotificationOption> {
        val notificationOptionList = ArrayList<NotificationOption>()
        notificationOption?.forEach {
            notificationOptionList.add(
                NotificationOption(
                    type = it.type(),
                    enabled = it.enabled()
                )
            )
        }
        return notificationOptionList
    }

    fun convertUserOptions(options: ViewerQuery.Options?): UserOptions {
        return UserOptions(
            titleLanguage = options?.titleLanguage(),
            displayAdultContent = options?.displayAdultContent(),
            airingNotifications = options?.airingNotifications(),
            notificationOptions = convertNotificationOptionList(options?.notificationOptions())
        )
    }

    fun convertUserOptions(options: AniListSettingsMutation.Options?): UserOptions {
        return UserOptions(
            titleLanguage = options?.titleLanguage(),
            displayAdultContent = options?.displayAdultContent(),
            airingNotifications = options?.airingNotifications(),
            notificationOptions = convertNotificationOptionListFromSettingsMutation(options?.notificationOptions())
        )
    }

    fun convertAnimeListMediaListTypeOptions(animeList: ViewerQuery.AnimeList?): MediaListTypeOptions {
        return MediaListTypeOptions(
            sectionOrder = animeList?.sectionOrder(),
            splitCompletedSectionByFormat = animeList?.splitCompletedSectionByFormat(),
            customLists = animeList?.customLists(),
            advancedScoring = animeList?.advancedScoring(),
            advancedScoringEnabled = animeList?.advancedScoringEnabled()
        )
    }

    fun convertMangaListMediaListTypeOptions(mangaList: ViewerQuery.MangaList?): MediaListTypeOptions {
        return MediaListTypeOptions(
            sectionOrder = mangaList?.sectionOrder(),
            splitCompletedSectionByFormat = mangaList?.splitCompletedSectionByFormat(),
            customLists = mangaList?.customLists(),
            advancedScoring = mangaList?.advancedScoring(),
            advancedScoringEnabled = mangaList?.advancedScoringEnabled()
        )
    }

    fun convertMediaListOptions(mediaListOptions: ViewerQuery.MediaListOptions?): MediaListOptions {
        return MediaListOptions(
            scoreFormat = mediaListOptions?.scoreFormat(),
            rowOrder = mediaListOptions?.rowOrder(),
            animeList = convertAnimeListMediaListTypeOptions(mediaListOptions?.animeList()),
            mangaList = convertMangaListMediaListTypeOptions(mediaListOptions?.mangaList())
        )
    }

    fun convertAnimeUserStatistics(anime: ViewerQuery.Anime?): UserStatistics {
        return UserStatistics(
            count = anime?.count()!!,
            meanScore = anime.meanScore(),
            standardDeviation = anime.standardDeviation(),
            minutesWatched = anime.minutesWatched(),
            episodesWatched = anime.episodesWatched(),
            chaptersRead = anime.chaptersRead(),
            volumesRead = anime.volumesRead()
        )
    }

    fun convertMangaUserStatistics(manga: ViewerQuery.Manga?): UserStatistics {
        return UserStatistics(
            count = manga?.count()!!,
            meanScore = manga.meanScore(),
            standardDeviation = manga.standardDeviation(),
            minutesWatched = manga.minutesWatched(),
            episodesWatched = manga.episodesWatched(),
            chaptersRead = manga.chaptersRead(),
            volumesRead = manga.volumesRead()
        )
    }

    fun convertUserStatisticTypes(statistics: ViewerQuery.Statistics?): UserStatisticTypes {
        return UserStatisticTypes(
            anime = convertAnimeUserStatistics(statistics?.anime()),
            manga = convertMangaUserStatistics(statistics?.manga())
        )
    }
}