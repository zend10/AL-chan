package com.zen.alchan.data.datasource

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.rx3.rxSingle
import com.zen.alchan.*
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.helper.enums.Sort
import com.zen.alchan.helper.enums.getAniListMediaSort
import com.zen.alchan.type.*
import io.reactivex.rxjava3.core.Observable

class DefaultContentDataSource(private val apolloHandler: ApolloHandler, private val statusVersion: Int, private val sourceVersion: Int) : ContentDataSource {

    override fun getHomeQuery(): Observable<ApolloResponse<HomeDataQuery.Data>> {
        val query = HomeDataQuery(statusVersion = Optional.present(statusVersion))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getGenres(): Observable<ApolloResponse<GenreQuery.Data>> {
        val query = GenreQuery()
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getTags(): Observable<ApolloResponse<TagQuery.Data>> {
        val query = TagQuery()
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun searchMedia(
        searchQuery: String,
        type: MediaType,
        mediaFilter: MediaFilter?,
        page: Int
    ): Observable<ApolloResponse<SearchMediaQuery.Data>> {
        val query = SearchMediaQuery(
            search = Optional.presentIfNotNull(searchQuery.ifBlank { null }),
            type = Optional.present(type),
            page = Optional.present(page),
            statusVersion = Optional.present(statusVersion),
            sourceVersion = Optional.present(sourceVersion),
            sort = Optional.present(mediaFilter?.let { listOf(mediaFilter.sort.getAniListMediaSort(mediaFilter.orderByDescending)) }),
            formatIn = Optional.presentIfNotNull(if (mediaFilter?.mediaFormats?.isNotEmpty() == true) mediaFilter.mediaFormats else null),
            statusIn = Optional.presentIfNotNull(if (mediaFilter?.mediaStatuses?.isNotEmpty() == true) mediaFilter.mediaStatuses else null),
            sourceIn = Optional.presentIfNotNull(if (mediaFilter?.mediaSources?.isNotEmpty() == true) mediaFilter.mediaSources else null),
            countryOfOrigin = Optional.presentIfNotNull(mediaFilter?.countries?.firstOrNull()?.iso),
            season = Optional.presentIfNotNull(mediaFilter?.mediaSeasons?.firstOrNull()),
            seasonYear = Optional.presentIfNotNull(mediaFilter?.seasonYear),
            startDateGreater = Optional.presentIfNotNull(mediaFilter?.minYear?.toString()?.padEnd(8, '0')),
            startDateLesser = Optional.presentIfNotNull(mediaFilter?.maxYear?.toString()?.plus("1231")),
            onList = Optional.presentIfNotNull(mediaFilter?.onList),
            genreIn = Optional.presentIfNotNull(if (mediaFilter?.includedGenres?.isNotEmpty() == true) mediaFilter.includedGenres else null),
            genreNotIn = Optional.presentIfNotNull(if (mediaFilter?.excludedGenres?.isNotEmpty() == true) mediaFilter.excludedGenres else null),
            minimumTagRank = Optional.presentIfNotNull(mediaFilter?.minTagPercentage),
            tagIn = Optional.presentIfNotNull(if (mediaFilter?.includedTags?.isNotEmpty() == true) mediaFilter.includedTags.map { it.name } else null),
            tagNotIn = Optional.presentIfNotNull(if (mediaFilter?.excludedTags?.isNotEmpty() == true) mediaFilter.excludedTags.map { it.name } else null),
            licensedByIdIn = Optional.presentIfNotNull(if (mediaFilter?.streamingOn?.isNotEmpty() == true) mediaFilter.streamingOn.map { it.id } else null),
            episodesGreater = Optional.presentIfNotNull(if (type == MediaType.ANIME) mediaFilter?.minEpisodes else null),
            episodesLesser = Optional.presentIfNotNull(if (type == MediaType.ANIME) mediaFilter?.maxEpisodes else null),
            durationGreater = Optional.presentIfNotNull(if (type == MediaType.ANIME) mediaFilter?.minDuration else null),
            durationLesser = Optional.presentIfNotNull(if (type == MediaType.ANIME) mediaFilter?.maxDuration else null),
            chaptersGreater = Optional.presentIfNotNull(if (type == MediaType.MANGA) mediaFilter?.minEpisodes else null),
            chaptersLesser = Optional.presentIfNotNull(if (type == MediaType.MANGA) mediaFilter?.maxEpisodes else null),
            volumesGreater = Optional.presentIfNotNull(if (type == MediaType.MANGA) mediaFilter?.minDuration else null),
            volumesLesser = Optional.presentIfNotNull(if (type == MediaType.MANGA) mediaFilter?.maxDuration else null),
            averageScoreGreater = Optional.presentIfNotNull(mediaFilter?.minAverageScore),
            averageScoreLesser = Optional.presentIfNotNull(mediaFilter?.maxAverageScore),
            popularityGreater = Optional.presentIfNotNull(mediaFilter?.minPopularity),
            popularityLesser = Optional.presentIfNotNull(mediaFilter?.maxPopularity),
            isLicensed = Optional.presentIfNotNull(mediaFilter?.isDoujin?.not())
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun searchCharacter(
        searchQuery: String,
        page: Int
    ): Observable<ApolloResponse<SearchCharacterQuery.Data>> {
        val query = SearchCharacterQuery(
            search = Optional.presentIfNotNull(searchQuery.ifBlank { null }),
            page = Optional.present(page),
            sort = Optional.present(listOf(CharacterSort.FAVOURITES_DESC))
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun searchStaff(
        searchQuery: String,
        page: Int
    ): Observable<ApolloResponse<SearchStaffQuery.Data>> {
        val query = SearchStaffQuery(
            search = Optional.presentIfNotNull(searchQuery.ifBlank { null }),
            page = Optional.present(page),
            sort = Optional.present(listOf(StaffSort.FAVOURITES_DESC))
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun searchStudio(
        searchQuery: String,
        page: Int
    ): Observable<ApolloResponse<SearchStudioQuery.Data>> {
        val query = SearchStudioQuery(
            search = Optional.presentIfNotNull(searchQuery.ifBlank { null }),
            page = Optional.present(page),
            sort = Optional.present(listOf(StudioSort.FAVOURITES_DESC))
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun searchUser(
        searchQuery: String,
        page: Int
    ): Observable<ApolloResponse<SearchUserQuery.Data>> {
        val query = SearchUserQuery(
            search = Optional.presentIfNotNull(searchQuery.ifBlank { null }),
            page = Optional.present(page)
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getSeasonal(
        page: Int,
        year: Int,
        season: MediaSeason,
        sort: Sort,
        orderByDescending: Boolean,
        onlyShowOnList: Boolean?,
        showAdult: Boolean
    ): Observable<ApolloResponse<SearchMediaQuery.Data>> {
        val query = SearchMediaQuery(
            type = Optional.present(MediaType.ANIME),
            page = Optional.present(page),
            statusVersion = Optional.present(statusVersion),
            sourceVersion = Optional.present(sourceVersion),
            seasonYear = Optional.present(year),
            season = Optional.present(season),
            sort = Optional.present(listOf(sort.getAniListMediaSort(orderByDescending))),
            onList = Optional.presentIfNotNull(onlyShowOnList),
            isAdult = Optional.present(showAdult)
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getAiringSchedule(
        page: Int,
        airingAtGreater: Int,
        airingAtLesser: Int
    ): Observable<ApolloResponse<AiringScheduleQuery.Data>> {
        val query = AiringScheduleQuery(
            page = Optional.present(page),
            airingAtGreater = Optional.present(airingAtGreater),
            airingAtLesser = Optional.present(airingAtLesser)
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }
}