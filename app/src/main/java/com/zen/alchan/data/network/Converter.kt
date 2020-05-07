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
            lists = convertAnimeMediaListGroupList(mediaListCollection?.lists())
        )
    }

    fun convertMediaListCollection(mediaListCollection: MangaListCollectionQuery.MediaListCollection?): MediaListCollection {
        return MediaListCollection(
            lists = convertMangaMediaListGroupList(mediaListCollection?.lists())
        )
    }

    fun convertAnimeMediaListGroupList(lists: List<AnimeListCollectionQuery.List>?): List<MediaListGroup> {
        val mediaListGroupList = ArrayList<MediaListGroup>()
        lists?.forEach {
            mediaListGroupList.add(
                MediaListGroup(
                    entries = convertAnimeMediaListList(it.entries()),
                    name = it.name(),
                    isCustomList = it.isCustomList,
                    isSplitCompletedList = it.isSplitCompletedList,
                    status = it.status()
                )
            )
        }
        return mediaListGroupList
    }

    fun convertMangaMediaListGroupList(lists: List<MangaListCollectionQuery.List>?): List<MediaListGroup> {
        val mediaListGroupList = ArrayList<MediaListGroup>()
        lists?.forEach {
            mediaListGroupList.add(
                MediaListGroup(
                    entries = convertMangaMediaListList(it.entries()),
                    name = it.name(),
                    isCustomList = it.isCustomList,
                    isSplitCompletedList = it.isSplitCompletedList,
                    status = it.status()
                )
            )
        }
        return mediaListGroupList
    }

    fun convertAnimeMediaListList(entries: List<AnimeListCollectionQuery.Entry>?): List<MediaList> {
        val mediaList = ArrayList<MediaList>()
        entries?.forEach {
            mediaList.add(
                MediaList(
                    id = it.id(),
                    status = it.status(),
                    score = it.score(),
                    progress = it.progress(),
                    progressVolumes = null,
                    repeat = it.repeat(),
                    priority = it.priority(),
                    private = null,
                    notes = null,
                    hiddenFromStatusList = null,
                    customLists = null,
                    advancedScores = it.advancedScores(),
                    startedAt = if (it.startedAt() != null) convertFuzzyDate(it.startedAt()!!) else null,
                    completedAt = if (it.completedAt() != null) convertFuzzyDate(it.completedAt()!!) else null,
                    updatedAt = null,
                    createdAt = null,
                    media = convertMedia(it.media()!!)
                )
            )
        }
        return mediaList
    }

    fun convertMangaMediaListList(entries: List<MangaListCollectionQuery.Entry>?): List<MediaList> {
        val mediaList = ArrayList<MediaList>()
        entries?.forEach {
            mediaList.add(
                MediaList(
                    id = it.id(),
                    status = it.status(),
                    score = it.score(),
                    progress = it.progress(),
                    progressVolumes = it.progressVolumes(),
                    repeat = it.repeat(),
                    priority = it.priority(),
                    private = null,
                    notes = null,
                    hiddenFromStatusList = null,
                    customLists = null,
                    advancedScores = it.advancedScores(),
                    startedAt = if (it.startedAt() != null) convertFuzzyDate(it.startedAt()!!) else null,
                    completedAt = if (it.completedAt() != null) convertFuzzyDate(it.completedAt()!!) else null,
                    updatedAt = null,
                    createdAt = null,
                    media = convertMedia(it.media()!!)
                )
            )
        }
        return mediaList
    }

    fun convertMediaList(entries: AnimeListEntryMutation.SaveMediaListEntry): MediaList {
        return MediaList(
            id = entries.id(),
            status = entries.status(),
            score = entries.score(),
            progress = entries.progress(),
            progressVolumes = null,
            repeat = entries.repeat(),
            priority = entries.priority(),
            private = entries.private_(),
            notes = entries.notes(),
            hiddenFromStatusList = entries.hiddenFromStatusLists(),
            customLists = entries.customLists(),
            advancedScores = entries.advancedScores(),
            startedAt = if (entries.startedAt() != null) convertFuzzyDate(entries.startedAt()!!) else null,
            completedAt = if (entries.completedAt() != null) convertFuzzyDate(entries.completedAt()!!) else null,
            updatedAt = entries.updatedAt(),
            createdAt = entries.createdAt(),
            media = convertMedia(entries.media()!!)
        )
    }

    fun convertMediaList(entries: MangaListEntryMutation.SaveMediaListEntry): MediaList {
        return MediaList(
            id = entries.id(),
            status = entries.status(),
            score = entries.score(),
            progress = entries.progress(),
            progressVolumes = entries.progressVolumes(),
            repeat = entries.repeat(),
            priority = entries.priority(),
            private = entries.private_(),
            notes = entries.notes(),
            hiddenFromStatusList = entries.hiddenFromStatusLists(),
            customLists = entries.customLists(),
            advancedScores = entries.advancedScores(),
            startedAt = if (entries.startedAt() != null) convertFuzzyDate(entries.startedAt()!!) else null,
            completedAt = if (entries.completedAt() != null) convertFuzzyDate(entries.completedAt()!!) else null,
            updatedAt = entries.updatedAt(),
            createdAt = entries.createdAt(),
            media = convertMedia(entries.media()!!)
        )
    }

    fun convertMediaList(entries: AnimeListQuery.MediaList): MediaList {
        return MediaList(
            id = entries.id(),
            status = entries.status(),
            score = entries.score(),
            progress = entries.progress(),
            progressVolumes = null,
            repeat = entries.repeat(),
            priority = entries.priority(),
            private = entries.private_(),
            notes = entries.notes(),
            hiddenFromStatusList = entries.hiddenFromStatusLists(),
            customLists = entries.customLists(),
            advancedScores = entries.advancedScores(),
            startedAt = if (entries.startedAt() != null) convertFuzzyDate(entries.startedAt()!!) else null,
            completedAt = if (entries.completedAt() != null) convertFuzzyDate(entries.completedAt()!!) else null,
            updatedAt = entries.updatedAt(),
            createdAt = entries.createdAt(),
            media = convertMedia(entries.media()!!)
        )
    }

    fun convertMediaList(entries: MangaListQuery.MediaList): MediaList {
        return MediaList(
            id = entries.id(),
            status = entries.status(),
            score = entries.score(),
            progress = entries.progress(),
            progressVolumes = entries.progressVolumes(),
            repeat = entries.repeat(),
            priority = entries.priority(),
            private = entries.private_(),
            notes = entries.notes(),
            hiddenFromStatusList = entries.hiddenFromStatusLists(),
            customLists = entries.customLists(),
            advancedScores = entries.advancedScores(),
            startedAt = if (entries.startedAt() != null) convertFuzzyDate(entries.startedAt()!!) else null,
            completedAt = if (entries.completedAt() != null) convertFuzzyDate(entries.completedAt()!!) else null,
            updatedAt = entries.updatedAt(),
            createdAt = entries.createdAt(),
            media = convertMedia(entries.media()!!)
        )
    }

    fun convertMedia(media: AnimeListCollectionQuery.Media): Media {
        return Media(
            id = media.id(),
            title = convertMediaTitle(mediaTitle = media.title()!!),
            type = media.type(),
            format = media.format(),
            status = media.status(),
            startDate = if (media.startDate() != null) convertFuzzyDate(media.startDate()!!) else null,
            season = media.season(),
            seasonYear = media.seasonYear(),
            episodes = media.episodes(),
            chapters = null,
            volumes = null,
            countryOfOrigin = media.countryOfOrigin(),
            source = media.source(),
            isFavourite = media.isFavourite,
            coverImage = if (media.coverImage() != null) convertMediaCoverImage(media.coverImage()!!) else null,
            genres = media.genres(),
            averageScore = media.averageScore(),
            popularity = media.popularity(),
            isAdult = media.isAdult,
            nextAiringEpisode = if (media.nextAiringEpisode() != null) convertNextAiringEpisode(media.nextAiringEpisode()!!) else null,
            siteUrl = null
        )
    }

    fun convertMedia(media: MangaListCollectionQuery.Media): Media {
        return Media(
            id = media.id(),
            title = convertMediaTitle(mediaTitle = media.title()!!),
            type = media.type(),
            format = media.format(),
            status = media.status(),
            startDate = if (media.startDate() != null) convertFuzzyDate(media.startDate()!!) else null,
            season = null,
            seasonYear = null,
            episodes = null,
            chapters = media.chapters(),
            volumes = media.volumes(),
            countryOfOrigin = media.countryOfOrigin(),
            source = media.source(),
            isFavourite = media.isFavourite,
            coverImage = if (media.coverImage() != null) convertMediaCoverImage(media.coverImage()!!) else null,
            genres = media.genres(),
            averageScore = media.averageScore(),
            popularity = media.popularity(),
            isAdult = media.isAdult,
            nextAiringEpisode = null,
            siteUrl = null
        )
    }

    fun convertMedia(media: AnimeListEntryMutation.Media): Media {
        return Media(
            id = media.id(),
            title = convertMediaTitle(mediaTitle = media.title()!!),
            type = media.type(),
            format = media.format(),
            status = media.status(),
            startDate = if (media.startDate() != null) convertFuzzyDate(media.startDate()!!) else null,
            season = media.season(),
            seasonYear = media.seasonYear(),
            episodes = media.episodes(),
            chapters = null,
            volumes = null,
            isFavourite = media.isFavourite,
            countryOfOrigin = media.countryOfOrigin(),
            source = media.source(),
            coverImage = if (media.coverImage() != null) convertMediaCoverImage(media.coverImage()!!) else null,
            genres = media.genres(),
            averageScore = media.averageScore(),
            popularity = media.popularity(),
            isAdult = media.isAdult,
            nextAiringEpisode = if (media.nextAiringEpisode() != null) convertNextAiringEpisode(media.nextAiringEpisode()!!) else null,
            siteUrl = media.siteUrl()
        )
    }

    fun convertMedia(media: MangaListEntryMutation.Media): Media {
        return Media(
            id = media.id(),
            title = convertMediaTitle(mediaTitle = media.title()!!),
            type = media.type(),
            format = media.format(),
            status = media.status(),
            startDate = if (media.startDate() != null) convertFuzzyDate(media.startDate()!!) else null,
            season = null,
            seasonYear = null,
            episodes = null,
            chapters = media.chapters(),
            volumes = media.volumes(),
            isFavourite = media.isFavourite,
            countryOfOrigin = media.countryOfOrigin(),
            source = media.source(),
            coverImage = if (media.coverImage() != null) convertMediaCoverImage(media.coverImage()!!) else null,
            genres = media.genres(),
            averageScore = media.averageScore(),
            popularity = media.popularity(),
            isAdult = media.isAdult,
            nextAiringEpisode = null,
            siteUrl = media.siteUrl()
        )
    }

    fun convertMedia(media: AnimeListQuery.Media): Media {
        return Media(
            id = media.id(),
            title = convertMediaTitle(mediaTitle = media.title()!!),
            type = media.type(),
            format = media.format(),
            status = media.status(),
            startDate = null,
            season = null,
            seasonYear = null,
            episodes = media.episodes(),
            chapters = null,
            volumes = null,
            isFavourite = media.isFavourite,
            countryOfOrigin = null,
            source = null,
            coverImage = null,
            genres = null,
            averageScore = null,
            popularity = null,
            isAdult = media.isAdult,
            nextAiringEpisode = null,
            siteUrl = media.siteUrl()
        )
    }

    fun convertMedia(media: MangaListQuery.Media): Media {
        return Media(
            id = media.id(),
            title = convertMediaTitle(mediaTitle = media.title()!!),
            type = media.type(),
            format = media.format(),
            status = media.status(),
            startDate = null,
            season = null,
            seasonYear = null,
            episodes = null,
            chapters = media.chapters(),
            volumes = media.volumes(),
            isFavourite = media.isFavourite,
            countryOfOrigin = null,
            source = null,
            coverImage = null,
            genres = null,
            averageScore = null,
            popularity = null,
            isAdult = media.isAdult,
            nextAiringEpisode = null,
            siteUrl = media.siteUrl()
        )
    }

    fun convertMediaTitle(mediaTitle: AnimeListCollectionQuery.Title): MediaTitle {
        return MediaTitle(mediaTitle.userPreferred()!!)
    }

    fun convertMediaTitle(mediaTitle: AnimeListEntryMutation.Title): MediaTitle {
        return MediaTitle(mediaTitle.userPreferred()!!)
    }

    fun convertMediaTitle(mediaTitle: AnimeListQuery.Title): MediaTitle {
        return MediaTitle(mediaTitle.userPreferred()!!)
    }

    fun convertMediaTitle(mediaTitle: MangaListCollectionQuery.Title): MediaTitle {
        return MediaTitle(mediaTitle.userPreferred()!!)
    }

    fun convertMediaTitle(mediaTitle: MangaListEntryMutation.Title): MediaTitle {
        return MediaTitle(mediaTitle.userPreferred()!!)
    }

    fun convertMediaTitle(mediaTitle: MangaListQuery.Title): MediaTitle {
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

    fun convertNextAiringEpisode(nextAiringEpisode: AnimeListEntryMutation.NextAiringEpisode): AiringSchedule {
        return AiringSchedule(
            id = nextAiringEpisode.id(),
            airingAt = nextAiringEpisode.airingAt(),
            timeUntilAiring = nextAiringEpisode.timeUntilAiring(),
            episode = nextAiringEpisode.episode(),
            mediaId = nextAiringEpisode.mediaId()
        )
    }

    fun convertMediaCoverImage(coverImage: AnimeListCollectionQuery.CoverImage): MediaCoverImage {
        return MediaCoverImage(coverImage.large())
    }

    fun convertMediaCoverImage(coverImage: AnimeListEntryMutation.CoverImage): MediaCoverImage {
        return MediaCoverImage(coverImage.large())
    }

    fun convertMediaCoverImage(coverImage: MangaListCollectionQuery.CoverImage): MediaCoverImage {
        return MediaCoverImage(coverImage.large())
    }

    fun convertMediaCoverImage(coverImage: MangaListEntryMutation.CoverImage): MediaCoverImage {
        return MediaCoverImage(coverImage.large())
    }

    fun convertFuzzyDate(fuzzyDate: AnimeListCollectionQuery.StartedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: AnimeListEntryMutation.StartedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: MangaListCollectionQuery.StartedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: MangaListEntryMutation.StartedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: AnimeListCollectionQuery.CompletedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: AnimeListEntryMutation.CompletedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: MangaListCollectionQuery.CompletedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: MangaListEntryMutation.CompletedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: AnimeListCollectionQuery.StartDate): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: AnimeListEntryMutation.StartDate): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: AnimeListQuery.StartedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: MangaListCollectionQuery.StartDate): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: MangaListEntryMutation.StartDate): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: MangaListQuery.StartedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: AnimeListQuery.CompletedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }

    fun convertFuzzyDate(fuzzyDate: MangaListQuery.CompletedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year(),
            fuzzyDate.month(),
            fuzzyDate.day()
        )
    }
}