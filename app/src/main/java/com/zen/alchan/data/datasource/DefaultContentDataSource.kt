package com.zen.alchan.data.datasource

import GenreQuery
import HomeDataQuery
import SearchCharacterQuery
import SearchMediaQuery
import SearchStaffQuery
import SearchStudioQuery
import SearchUserQuery
import TagQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.helper.enums.getAniListMediaSort
import io.reactivex.Observable
import type.*

class DefaultContentDataSource(private val apolloHandler: ApolloHandler, private val statusVersion: Int) : ContentDataSource {

    override fun getHomeQuery(): Observable<Response<HomeDataQuery.Data>> {
        val query = HomeDataQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getGenres(): Observable<Response<GenreQuery.Data>> {
        val query = GenreQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getTags(): Observable<Response<TagQuery.Data>> {
        val query = TagQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchMedia(
        searchQuery: String,
        type: MediaType,
        mediaFilter: MediaFilter?,
        page: Int
    ): Observable<Response<SearchMediaQuery.Data>> {
        val query = SearchMediaQuery(
            search = Input.optional(searchQuery.ifBlank { null }),
            type = Input.fromNullable(type),
            page = Input.fromNullable(page),
            statusVersion = Input.fromNullable(statusVersion),
            sort = Input.fromNullable(mediaFilter?.let { listOf(mediaFilter.sort.getAniListMediaSort(mediaFilter.orderByDescending)) }),
            formatIn = Input.optional(if (mediaFilter?.mediaFormats?.isNotEmpty() == true) mediaFilter.mediaFormats else null),
            statusIn = Input.optional(if (mediaFilter?.mediaStatuses?.isNotEmpty() == true) mediaFilter.mediaStatuses else null),
            sourceIn = Input.optional(if (mediaFilter?.mediaSources?.isNotEmpty() == true) mediaFilter.mediaSources else null),
            countryOfOrigin = Input.optional(mediaFilter?.countries?.firstOrNull()?.iso),
            season = Input.optional(mediaFilter?.mediaSeasons?.firstOrNull()),
            startDateGreater = Input.optional(mediaFilter?.minYear?.toString()?.padEnd(8, '0')),
            startDateLesser = Input.optional(mediaFilter?.maxYear?.toString()?.padEnd(8, '0')),
            onList = Input.optional(mediaFilter?.onList),
            genreIn = Input.optional(if (mediaFilter?.includedGenres?.isNotEmpty() == true) mediaFilter.includedGenres else null),
            genreNotIn = Input.optional(if (mediaFilter?.excludedGenres?.isNotEmpty() == true) mediaFilter.excludedGenres else null),
            minimumTagRank = Input.optional(mediaFilter?.minTagPercentage),
            tagIn = Input.optional(if (mediaFilter?.includedTags?.isNotEmpty() == true) mediaFilter.includedTags.map { it.name } else null),
            tagNotIn = Input.optional(if (mediaFilter?.excludedTags?.isNotEmpty() == true) mediaFilter.excludedTags.map { it.name } else null),
            licensedByIn = Input.optional(if (mediaFilter?.streamingOn?.isNotEmpty() == true) mediaFilter.streamingOn.map { it.key } else null),
            episodesGreater = Input.optional(if (type == MediaType.ANIME) mediaFilter?.minEpisodes else null),
            episodesLesser = Input.optional(if (type == MediaType.ANIME) mediaFilter?.maxEpisodes else null),
            durationGreater = Input.optional(if (type == MediaType.ANIME) mediaFilter?.minDuration else null),
            durationLesser = Input.optional(if (type == MediaType.ANIME) mediaFilter?.maxDuration else null),
            chaptersGreater = Input.optional(if (type == MediaType.MANGA) mediaFilter?.minEpisodes else null),
            chaptersLesser = Input.optional(if (type == MediaType.MANGA) mediaFilter?.maxEpisodes else null),
            volumesGreater = Input.optional(if (type == MediaType.MANGA) mediaFilter?.minDuration else null),
            volumesLesser = Input.optional(if (type == MediaType.MANGA) mediaFilter?.maxDuration else null),
            averageScoreGreater = Input.optional(mediaFilter?.minAverageScore),
            averageScoreLesser = Input.optional(mediaFilter?.maxAverageScore),
            popularityGreater = Input.optional(mediaFilter?.minPopularity),
            popularityLesser = Input.optional(mediaFilter?.maxPopularity),
            isLicensed = Input.optional(mediaFilter?.isDoujin?.not())
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchCharacter(
        searchQuery: String,
        page: Int
    ): Observable<Response<SearchCharacterQuery.Data>> {
        val query = SearchCharacterQuery(
            search = Input.optional(searchQuery.ifBlank { null }),
            page = Input.fromNullable(page),
            sort = Input.fromNullable(listOf(CharacterSort.FAVOURITES_DESC))
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchStaff(
        searchQuery: String,
        page: Int
    ): Observable<Response<SearchStaffQuery.Data>> {
        val query = SearchStaffQuery(
            search = Input.optional(searchQuery.ifBlank { null }),
            page = Input.fromNullable(page),
            sort = Input.fromNullable(listOf(StaffSort.FAVOURITES_DESC))
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchStudio(
        searchQuery: String,
        page: Int
    ): Observable<Response<SearchStudioQuery.Data>> {
        val query = SearchStudioQuery(
            search = Input.optional(searchQuery.ifBlank { null }),
            page = Input.fromNullable(page),
            sort = Input.fromNullable(listOf(StudioSort.FAVOURITES_DESC))
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchUser(
        searchQuery: String,
        page: Int
    ): Observable<Response<SearchUserQuery.Data>> {
        val query = SearchUserQuery(
            search = Input.optional(searchQuery.ifBlank { null }),
            page = Input.fromNullable(page)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }
}