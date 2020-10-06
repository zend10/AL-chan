package com.zen.alchan.data.network

import com.zen.alchan.data.response.*

object Converter {

    fun convertUser(viewer: ViewerQuery.Viewer?): User {
        return User(
            id = viewer?.id!!,
            name = viewer.name,
            about = viewer.about,
            avatar = if (viewer.avatar != null) {
                UserAvatar(
                    large = viewer.avatar.large,
                    medium = viewer.avatar.medium
                )
            } else null,
            bannerImage = viewer.bannerImage,
            options = if (viewer.options != null) {
                UserOptions(
                    titleLanguage = viewer.options.titleLanguage,
                    displayAdultContent = viewer.options.displayAdultContent,
                    airingNotifications = viewer.options.airingNotifications,
                    notificationOptions = if (!viewer.options.notificationOptions.isNullOrEmpty()) {
                        viewer.options.notificationOptions.filterNotNull().map { NotificationOption(it.type, it.enabled) }
                    } else null
                )
            } else null,
            mediaListOptions = if (viewer.mediaListOptions != null) MediaListOptions(
                scoreFormat = viewer.mediaListOptions.scoreFormat,
                rowOrder = viewer.mediaListOptions.rowOrder,
                animeList = if (viewer.mediaListOptions.animeList != null) {
                    MediaListTypeOptions(
                        sectionOrder = viewer.mediaListOptions.animeList.sectionOrder,
                        splitCompletedSectionByFormat = viewer.mediaListOptions.animeList.splitCompletedSectionByFormat,
                        customLists = viewer.mediaListOptions.animeList.customLists,
                        advancedScoring = viewer.mediaListOptions.animeList.advancedScoring,
                        advancedScoringEnabled = viewer.mediaListOptions.animeList.advancedScoringEnabled
                    )
                } else null,
                mangaList = if (viewer.mediaListOptions.mangaList != null) {
                    MediaListTypeOptions(
                        sectionOrder = viewer.mediaListOptions.mangaList.sectionOrder,
                        splitCompletedSectionByFormat = viewer.mediaListOptions.mangaList.splitCompletedSectionByFormat,
                        customLists = viewer.mediaListOptions.mangaList.customLists,
                        advancedScoring = viewer.mediaListOptions.mangaList.advancedScoring,
                        advancedScoringEnabled = viewer.mediaListOptions.mangaList.advancedScoringEnabled
                    )
                } else null
            ) else null,
            statistics = if (viewer.statistics != null) {
                UserStatisticTypes(
                    anime = if (viewer.statistics.anime != null) {
                        UserStatistics(
                            count = viewer.statistics.anime.count,
                            meanScore = viewer.statistics.anime.meanScore,
                            standardDeviation = viewer.statistics.anime.standardDeviation,
                            minutesWatched = viewer.statistics.anime.minutesWatched,
                            episodesWatched = viewer.statistics.anime.episodesWatched,
                            chaptersRead = viewer.statistics.anime.chaptersRead,
                            volumesRead = viewer.statistics.anime.volumesRead
                        )
                    } else null,
                    manga = if (viewer.statistics.manga != null) {
                        UserStatistics(
                            count = viewer.statistics.manga.count,
                            meanScore = viewer.statistics.manga.meanScore,
                            standardDeviation = viewer.statistics.manga.standardDeviation,
                            minutesWatched = viewer.statistics.manga.minutesWatched,
                            episodesWatched = viewer.statistics.manga.episodesWatched,
                            chaptersRead = viewer.statistics.manga.chaptersRead,
                            volumesRead = viewer.statistics.manga.volumesRead
                        )
                    } else null
                )
            } else null,
            unreadNotificationCount = viewer.unreadNotificationCount,
            donatorTier = viewer.donatorTier,
            donatorBadge = viewer.donatorBadge,
            moderatorStatus = viewer.moderatorStatus,
            siteUrl = viewer.siteUrl
        )
    }

    fun convertUserOptions(options: AniListSettingsMutation.Options?): UserOptions {
        return UserOptions(
            titleLanguage = options?.titleLanguage,
            displayAdultContent = options?.displayAdultContent,
            airingNotifications = options?.airingNotifications,
            notificationOptions = options?.notificationOptions?.filterNotNull()?.map { NotificationOption(it.type, it.enabled) }
        )
    }

    fun convertMediaListOptions(mediaListOptions: ListSettingsMutation.MediaListOptions?): MediaListOptions {
        return MediaListOptions(
            scoreFormat = mediaListOptions?.scoreFormat,
            rowOrder = mediaListOptions?.rowOrder,
            animeList = MediaListTypeOptions(
                sectionOrder = mediaListOptions?.animeList?.sectionOrder,
                splitCompletedSectionByFormat = mediaListOptions?.animeList?.splitCompletedSectionByFormat,
                customLists = mediaListOptions?.animeList?.customLists,
                advancedScoring = mediaListOptions?.animeList?.advancedScoring,
                advancedScoringEnabled = mediaListOptions?.animeList?.advancedScoringEnabled
            ),
            mangaList = MediaListTypeOptions(
                sectionOrder = mediaListOptions?.mangaList?.sectionOrder,
                splitCompletedSectionByFormat = mediaListOptions?.mangaList?.splitCompletedSectionByFormat,
                customLists = mediaListOptions?.mangaList?.customLists,
                advancedScoring = mediaListOptions?.mangaList?.advancedScoring,
                advancedScoringEnabled = mediaListOptions?.mangaList?.advancedScoringEnabled
            )
        )
    }

    fun convertMediaListCollection(mediaListCollection: AnimeListCollectionQuery.MediaListCollection?): MediaListCollection {
        return MediaListCollection(
            mediaListCollection?.lists?.map {
                MediaListGroup(
                    entries = convertAnimeMediaListList(it?.entries),
                    name = it?.name,
                    isCustomList = it?.isCustomList,
                    isSplitCompletedList = it?.isSplitCompletedList,
                    status = it?.status
                )
            }
        )
    }

    fun convertMediaListCollection(mediaListCollection: MangaListCollectionQuery.MediaListCollection?): MediaListCollection {
        return MediaListCollection(
            mediaListCollection?.lists?.map {
                MediaListGroup(
                    entries = convertMangaMediaListList(it?.entries),
                    name = it?.name,
                    isCustomList = it?.isCustomList,
                    isSplitCompletedList = it?.isSplitCompletedList,
                    status = it?.status
                )
            }
        )
    }

    private fun convertAnimeMediaListList(entries: List<AnimeListCollectionQuery.Entry?>?): List<MediaList> {
        val mediaList = ArrayList<MediaList>()
        entries?.forEach {
            mediaList.add(
                MediaList(
                    id = it?.id!!,
                    status = it.status,
                    score = it.score,
                    progress = it.progress,
                    progressVolumes = null,
                    repeat = it.repeat,
                    priority = it.priority,
                    private = it.private_,
                    notes = it.notes,
                    hiddenFromStatusList = it.hiddenFromStatusLists,
                    customLists = it.customLists,
                    advancedScores = it.advancedScores,
                    startedAt = if (it.startedAt != null) FuzzyDate(it.startedAt.year, it.startedAt.month, it.startedAt.day) else null,
                    completedAt = if (it.completedAt != null) FuzzyDate(it.completedAt.year, it.completedAt.month, it.completedAt.day) else null,
                    updatedAt = it.updatedAt,
                    createdAt = it.createdAt,
                    media = convertMedia(it.media!!)
                )
            )
        }
        return mediaList
    }

    private fun convertMangaMediaListList(entries: List<MangaListCollectionQuery.Entry?>?): List<MediaList> {
        val mediaList = ArrayList<MediaList>()
        entries?.forEach {
            mediaList.add(
                MediaList(
                    id = it?.id!!,
                    status = it.status,
                    score = it.score,
                    progress = it.progress,
                    progressVolumes = it.progressVolumes,
                    repeat = it.repeat,
                    priority = it.priority,
                    private = it.private_,
                    notes = it.notes,
                    hiddenFromStatusList = it.hiddenFromStatusLists,
                    customLists = it.customLists,
                    advancedScores = it.advancedScores,
                    startedAt = if (it.startedAt != null) FuzzyDate(it.startedAt.year, it.startedAt.month, it.startedAt.day) else null,
                    completedAt = if (it.completedAt != null) FuzzyDate(it.completedAt.year, it.completedAt.month, it.completedAt.day) else null,
                    updatedAt = it.updatedAt,
                    createdAt = it.createdAt,
                    media = convertMedia(it.media!!)
                )
            )
        }
        return mediaList
    }

    fun convertMediaList(entries: AnimeListEntryMutation.SaveMediaListEntry): MediaList {
        return MediaList(
            id = entries.id,
            status = entries.status,
            score = entries.score,
            progress = entries.progress,
            progressVolumes = null,
            repeat = entries.repeat,
            priority = entries.priority,
            private = entries.private_,
            notes = entries.notes,
            hiddenFromStatusList = entries.hiddenFromStatusLists,
            customLists = entries.customLists,
            advancedScores = entries.advancedScores,
            startedAt = if (entries.startedAt != null) FuzzyDate(entries.startedAt.year, entries.startedAt.month, entries.startedAt.day) else null,
            completedAt = if (entries.completedAt != null) FuzzyDate(entries.completedAt.year, entries.completedAt.month, entries.completedAt.day) else null,
            updatedAt = entries.updatedAt,
            createdAt = entries.createdAt,
            media = convertMedia(entries.media!!)
        )
    }

    fun convertMediaList(entries: MangaListEntryMutation.SaveMediaListEntry): MediaList {
        return MediaList(
            id = entries.id,
            status = entries.status,
            score = entries.score,
            progress = entries.progress,
            progressVolumes = entries.progressVolumes,
            repeat = entries.repeat,
            priority = entries.priority,
            private = entries.private_,
            notes = entries.notes,
            hiddenFromStatusList = entries.hiddenFromStatusLists,
            customLists = entries.customLists,
            advancedScores = entries.advancedScores,
            startedAt = if (entries.startedAt != null) FuzzyDate(entries.startedAt.year, entries.startedAt.month, entries.startedAt.day) else null,
            completedAt = if (entries.completedAt != null) FuzzyDate(entries.completedAt.year, entries.completedAt.month, entries.completedAt.day) else null,
            updatedAt = entries.updatedAt,
            createdAt = entries.createdAt,
            media = convertMedia(entries.media!!)
        )
    }

    fun convertMediaList(entries: AnimeListQuery.MediaList): MediaList {
        return MediaList(
            id = entries.id,
            status = entries.status,
            score = entries.score,
            progress = entries.progress,
            progressVolumes = null,
            repeat = entries.repeat,
            priority = entries.priority,
            private = entries.private_,
            notes = entries.notes,
            hiddenFromStatusList = entries.hiddenFromStatusLists,
            customLists = entries.customLists,
            advancedScores = entries.advancedScores,
            startedAt = if (entries.startedAt != null) FuzzyDate(entries.startedAt.year, entries.startedAt.month, entries.startedAt.day) else null,
            completedAt = if (entries.completedAt != null) FuzzyDate(entries.completedAt.year, entries.completedAt.month, entries.completedAt.day) else null,
            updatedAt = entries.updatedAt,
            createdAt = entries.createdAt,
            media = convertMedia(entries.media!!)
        )
    }

    fun convertMediaList(entries: MangaListQuery.MediaList): MediaList {
        return MediaList(
            id = entries.id,
            status = entries.status,
            score = entries.score,
            progress = entries.progress,
            progressVolumes = entries.progressVolumes,
            repeat = entries.repeat,
            priority = entries.priority,
            private = entries.private_,
            notes = entries.notes,
            hiddenFromStatusList = entries.hiddenFromStatusLists,
            customLists = entries.customLists,
            advancedScores = entries.advancedScores,
            startedAt = if (entries.startedAt != null) FuzzyDate(entries.startedAt.year, entries.startedAt.month, entries.startedAt.day) else null,
            completedAt = if (entries.completedAt != null) FuzzyDate(entries.completedAt.year, entries.completedAt.month, entries.completedAt.day) else null,
            updatedAt = entries.updatedAt,
            createdAt = entries.createdAt,
            media = convertMedia(entries.media!!)
        )
    }

    private fun convertMediaList(entries: SeasonalAnimeQuery.MediaListEntry): MediaList {
        return MediaList(
            id = entries.id,
            status = entries.status,
            score = null,
            progress = null,
            progressVolumes = null,
            repeat = null,
            priority = null,
            private = null,
            notes = null,
            hiddenFromStatusList = null,
            customLists = null,
            advancedScores = null,
            startedAt = null,
            completedAt = null,
            updatedAt = null,
            createdAt = null,
            media = null
        )
    }

    private fun convertMedia(media: AnimeListCollectionQuery.Media): Media {
        return Media(
            id = media.id,
            title = MediaTitle(
                media.title?.romaji,
                media.title?.english,
                media.title?.native_,
                media.title?.userPreferred!!
            ),
            type = media.type,
            format = media.format,
            status = media.status,
            startDate = if (media.startDate != null) {
                FuzzyDate(media.startDate.year, media.startDate.month, media.startDate.day)
            } else null,
            season = media.season,
            seasonYear = media.seasonYear,
            episodes = media.episodes,
            duration = media.duration,
            chapters = null,
            volumes = null,
            countryOfOrigin = media.countryOfOrigin,
            source = media.source,
            isFavourite = media.isFavourite,
            coverImage = if (media.coverImage != null) {
                MediaCoverImage(media.coverImage.large, null)
            } else null,
            genres = media.genres,
            synonyms = media.synonyms,
            averageScore = media.averageScore,
            popularity = media.popularity,
            tags = if (!media.tags.isNullOrEmpty()) {
                media.tags.filterNotNull().map { MediaTag(it.name) }
            } else null,
            isAdult = media.isAdult,
            nextAiringEpisode = if (media.nextAiringEpisode != null) {
                AiringSchedule(
                    id = media.nextAiringEpisode.id,
                    airingAt = media.nextAiringEpisode.airingAt,
                    timeUntilAiring = media.nextAiringEpisode.timeUntilAiring,
                    episode = media.nextAiringEpisode.episode,
                    mediaId = media.nextAiringEpisode.mediaId
                )
            } else null,
            externalLinks = if (!media.externalLinks.isNullOrEmpty()) {
                media.externalLinks.filterNotNull().map { MediaExternalLinks(it.site) }
            } else null,
            siteUrl = null
        )
    }

    private fun convertMedia(media: MangaListCollectionQuery.Media): Media {
        return Media(
            id = media.id,
            title = MediaTitle(
                media.title?.romaji,
                media.title?.english,
                media.title?.native_,
                media.title?.userPreferred!!
            ),
            type = media.type,
            format = media.format,
            status = media.status,
            startDate = if (media.startDate != null) {
                FuzzyDate(media.startDate.year, media.startDate.month, media.startDate.day)
            } else null,
            season = null,
            seasonYear = null,
            episodes = null,
            chapters = media.chapters,
            volumes = media.volumes,
            countryOfOrigin = media.countryOfOrigin,
            source = media.source,
            isFavourite = media.isFavourite,
            coverImage = if (media.coverImage != null) {
                MediaCoverImage(media.coverImage.large, null)
            } else null,
            genres = media.genres,
            synonyms = media.synonyms,
            averageScore = media.averageScore,
            popularity = media.popularity,
            tags = if (!media.tags.isNullOrEmpty()) {
                media.tags.filterNotNull().map { MediaTag(it.name) }
            } else null,
            isAdult = media.isAdult,
            nextAiringEpisode = null,
            externalLinks = if (!media.externalLinks.isNullOrEmpty()) {
                media.externalLinks.filterNotNull().map { MediaExternalLinks(it.site) }
            } else null,
            siteUrl = null
        )
    }

    private fun convertMedia(media: AnimeListEntryMutation.Media): Media {
        return Media(
            id = media.id,
            title = MediaTitle(
                media.title?.romaji,
                media.title?.english,
                media.title?.native_,
                media.title?.userPreferred!!
            ),
            type = media.type,
            format = media.format,
            status = media.status,
            startDate = if (media.startDate != null) {
                FuzzyDate(media.startDate.year, media.startDate.month, media.startDate.day)
            } else null,
            season = media.season,
            seasonYear = media.seasonYear,
            episodes = media.episodes,
            chapters = null,
            volumes = null,
            isFavourite = media.isFavourite,
            countryOfOrigin = media.countryOfOrigin,
            source = media.source,
            coverImage = if (media.coverImage != null) {
                MediaCoverImage(media.coverImage.large, null)
            } else null,
            genres = media.genres,
            synonyms = media.synonyms,
            averageScore = media.averageScore,
            popularity = media.popularity,
            tags = null,
            isAdult = media.isAdult,
            nextAiringEpisode = if (media.nextAiringEpisode != null) {
                AiringSchedule(
                    id = media.nextAiringEpisode.id,
                    airingAt = media.nextAiringEpisode.airingAt,
                    timeUntilAiring = media.nextAiringEpisode.timeUntilAiring,
                    episode = media.nextAiringEpisode.episode,
                    mediaId = media.nextAiringEpisode.mediaId
                )
            } else null,
            externalLinks = if (!media.externalLinks.isNullOrEmpty()) {
                media.externalLinks.filterNotNull().map { MediaExternalLinks(it.site) }
            } else null,
            siteUrl = media.siteUrl
        )
    }

    private fun convertMedia(media: MangaListEntryMutation.Media): Media {
        return Media(
            id = media.id,
            title = MediaTitle(
                media.title?.romaji,
                media.title?.english,
                media.title?.native_,
                media.title?.userPreferred!!
            ),
            type = media.type,
            format = media.format,
            status = media.status,
            startDate = if (media.startDate != null) {
                FuzzyDate(media.startDate.year, media.startDate.month, media.startDate.day)
            } else null,
            season = null,
            seasonYear = null,
            episodes = null,
            chapters = media.chapters,
            volumes = media.volumes,
            isFavourite = media.isFavourite,
            countryOfOrigin = media.countryOfOrigin,
            source = media.source,
            coverImage = if (media.coverImage != null) {
                MediaCoverImage(media.coverImage.large, null)
            } else null,
            genres = media.genres,
            synonyms = media.synonyms,
            averageScore = media.averageScore,
            popularity = media.popularity,
            tags = null,
            isAdult = media.isAdult,
            nextAiringEpisode = null,
            externalLinks = if (!media.externalLinks.isNullOrEmpty()) {
                media.externalLinks.filterNotNull().map { MediaExternalLinks(it.site) }
            } else null,
            siteUrl = media.siteUrl
        )
    }

    private fun convertMedia(media: AnimeListQuery.Media): Media {
        return Media(
            id = media.id,
            title = MediaTitle(null, null, null, media.title?.userPreferred!!),
            type = media.type,
            format = media.format,
            status = media.status,
            startDate = null,
            season = null,
            seasonYear = null,
            episodes = media.episodes,
            chapters = null,
            volumes = null,
            isFavourite = media.isFavourite,
            countryOfOrigin = null,
            source = null,
            coverImage = null,
            genres = null,
            synonyms = null,
            averageScore = null,
            popularity = null,
            isAdult = media.isAdult,
            nextAiringEpisode = null,
            siteUrl = media.siteUrl
        )
    }

    private fun convertMedia(media: MangaListQuery.Media): Media {
        return Media(
            id = media.id,
            title = MediaTitle(null, null, null, media.title?.userPreferred!!),
            type = media.type,
            format = media.format,
            status = media.status,
            startDate = null,
            season = null,
            seasonYear = null,
            episodes = null,
            chapters = media.chapters,
            volumes = media.volumes,
            isFavourite = media.isFavourite,
            countryOfOrigin = null,
            source = null,
            coverImage = null,
            genres = null,
            synonyms = null,
            averageScore = null,
            popularity = null,
            isAdult = media.isAdult,
            nextAiringEpisode = null,
            siteUrl = media.siteUrl
        )
    }

    fun convertFuzzyDate(fuzzyDate: UserMediaListCollectionQuery.StartedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year,
            fuzzyDate.month,
            fuzzyDate.day
        )
    }

    fun convertFuzzyDate(fuzzyDate: UserMediaListCollectionQuery.CompletedAt): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year,
            fuzzyDate.month,
            fuzzyDate.day
        )
    }

    fun convertFuzzyDate(fuzzyDate: UserMediaListCollectionQuery.StartDate): FuzzyDate {
        return FuzzyDate(
            fuzzyDate.year,
            fuzzyDate.month,
            fuzzyDate.day
        )
    }

    fun convertMediaTagCollection(mediaTagCollections: List<TagQuery.MediaTagCollection?>): List<MediaTagCollection> {
        val mediaTagCollectionList = ArrayList<MediaTagCollection>()
        mediaTagCollections.forEach {
            if (it != null) mediaTagCollectionList.add(MediaTagCollection(it.id, it.name, it.description, it.category))
        }
        return mediaTagCollectionList
    }

    fun convertSeasonalAnime(media: List<SeasonalAnimeQuery.Medium?>?): List<SeasonalAnime> {
        val seasonalAnimeList = ArrayList<SeasonalAnime>()
        media?.forEach {
            seasonalAnimeList.add(
                SeasonalAnime(
                    id = it?.id!!,
                    title = MediaTitle(null, null, null, it.title?.userPreferred!!),
                    coverImage = MediaCoverImage(it.coverImage?.large, null),
                    format = it.format,
                    source = it.source,
                    episodes = it.episodes,
                    averageScore = it.averageScore,
                    favourites = it.favourites,
                    description = it.description,
                    startDate = if (it.startDate != null) FuzzyDate(it.startDate.year, it.startDate.month, it.startDate.day) else null,
                    genres = it.genres,
                    studios = if (it.studios != null) convertStudioConnection(it.studios) else null,
                    stats = if (it.stats != null) convertMediaStats(it.stats) else null,
                    mediaListEntry = if (it.mediaListEntry != null) convertMediaList(it.mediaListEntry) else null
                )
            )
        }
        return seasonalAnimeList
    }

    private fun convertStudioConnection(studioConnection: SeasonalAnimeQuery.Studios): StudioConnection {
        return StudioConnection(edges = convertStudioEdge(studioConnection.edges))
    }

    private fun convertStudioEdge(studioEdges: List<SeasonalAnimeQuery.Edge?>?): List<StudioEdge> {
        val edges = ArrayList<StudioEdge>()
        studioEdges?.forEach { edges.add(StudioEdge(node = convertStudioNode(it?.node!!))) }
        return edges
    }

    private fun convertStudioNode(studioNode: SeasonalAnimeQuery.Node): Studio {
        return Studio(id = studioNode.id, name = studioNode.name)
    }

    private fun convertMediaStats(stats: SeasonalAnimeQuery.Stats): MediaStats {
        return MediaStats(statusDistribution = convertStatusDistribution(statusDistributions = stats.statusDistribution))
    }

    private fun convertStatusDistribution(statusDistributions: List<SeasonalAnimeQuery.StatusDistribution?>?): List<StatusDistribution> {
        val statusDistributionList = ArrayList<StatusDistribution>()
        statusDistributions?.forEach { statusDistributionList.add(StatusDistribution(status = it?.status!!, amount = it.amount!!)) }
        return statusDistributionList
    }
}