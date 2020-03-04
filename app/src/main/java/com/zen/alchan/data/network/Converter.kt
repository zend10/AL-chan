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

    fun convertAnimeListMediaListTypeOptions(animeList: ListSettingsMutation.AnimeList?): MediaListTypeOptions {
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

    fun convertMangaListMediaListTypeOptions(mangaList: ListSettingsMutation.MangaList?): MediaListTypeOptions {
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

    fun convertMediaListOptions(mediaListOptions: ListSettingsMutation.MediaListOptions?): MediaListOptions {
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

    fun convertMediaListCollection(mediaListCollection: AnimeListCollectionQuery.MediaListCollection?): MediaListCollection {
        return MediaListCollection(
            lists = convertMediaListGroupList(mediaListCollection?.lists())
        )
    }

    fun convertMediaListGroupList(lists: List<AnimeListCollectionQuery.List>?): List<MediaListGroup> {
        val mediaListGroupList = ArrayList<MediaListGroup>()
        lists?.forEach {
            mediaListGroupList.add(
                MediaListGroup(
                    entries = convertMediaList(it.entries()),
                    name = it.name(),
                    isCustomList = it.isCustomList,
                    isSplitCompletedList = it.isSplitCompletedList,
                    status = it.status()
                )
            )
        }
        return mediaListGroupList
    }

    fun convertMediaList(entries: List<AnimeListCollectionQuery.Entry>?): List<MediaList> {
        val mediaList = ArrayList<MediaList>()
        entries?.forEach {
            mediaList.add(
                MediaList(
                    id = it.id(),
                    status = it.status(),
                    score = it.score(),
                    progress = it.progress(),
                    priority = it.priority(),
                    private = it.private_(),
                    hiddenFromStatusList = it.hiddenFromStatusLists(),
                    media = convertMedia(it.media()!!)
                )
            )
        }
        return mediaList
    }

    fun convertMedia(media: AnimeListCollectionQuery.Media): Media {
        return Media(
            id = media.id(),
            title = convertMediaTitle(mediaTitle = media.title()!!),
            format = media.format(),
            status = media.status(),
            episodes = media.episodes(),
            coverImage = if (media.coverImage() != null) convertMediaCoverImage(media.coverImage()!!) else null,
            isFavourite = media.isFavourite,
            isAdult = media.isAdult,
            nextAiringEpisode = if (media.nextAiringEpisode() != null) convertNextAiringEpisode(media.nextAiringEpisode()!!) else null
        )
    }

    fun convertMediaTitle(mediaTitle: AnimeListCollectionQuery.Title): MediaTitle {
        return MediaTitle(mediaTitle.userPreferred()!!)
    }

    fun convertNextAiringEpisode(nextAiringEpisode: AnimeListCollectionQuery.NextAiringEpisode): AiringSchedule {
        return AiringSchedule(
            id = nextAiringEpisode.id(),
            airingAt = nextAiringEpisode.airingAt(),
            timeUntilAiring = nextAiringEpisode.timeUntilAiring(),
            episode = nextAiringEpisode.episode(),
            mediaId = nextAiringEpisode.mediaId()
        )
    }

    fun convertMediaCoverImage(coverImage: AnimeListCollectionQuery.CoverImage): MediaCoverImage {
        return MediaCoverImage(coverImage.extraLarge(), coverImage.large())
    }
}