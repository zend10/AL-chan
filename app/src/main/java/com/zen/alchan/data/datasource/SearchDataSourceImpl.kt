package com.zen.alchan.data.datasource

import AiringScheduleQuery
import CharacterImageQuery
import MediaImageQuery
import SearchAnimeQuery
import SearchCharactersQuery
import SearchMangaQuery
import SearchStaffsQuery
import SearchStudiosQuery
import SearchUsersQuery
import SeasonalAnimeQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.internal.format
import type.*

class SearchDataSourceImpl(private val apolloHandler: ApolloHandler) : SearchDataSource {

    override fun searchAnime(
        page: Int,
        search: String?,
        sort: List<MediaSort>?,
        formatIn: List<MediaFormat>?,
        statusIn: List<MediaStatus>?,
        sourceIn: List<MediaSource>?,
        countryOfOrigin: String?,
        season: MediaSeason?,
        startDateGreater: Int?,
        startDateLesser: Int?,
        isAdult: Boolean?,
        onList: Boolean?,
        genreIn: List<String>?,
        genreNotIn: List<String>?,
        minimumTagRank: Int?,
        tagIn: List<String>?,
        tagNotIn: List<String>?,
        licensedByIn: List<String>?,
        episodesGreater: Int?,
        episodesLesser: Int?,
        durationGreater: Int?,
        durationLesser: Int?,
        averageScoreGreater: Int?,
        averageScoreLesser: Int?,
        popularityGreater: Int?,
        popularityLesser: Int?
    ): Observable<Response<SearchAnimeQuery.Data>> {
        val checkSearch = if (search.isNullOrBlank()) null else search
        val checkSort = if (sort.isNullOrEmpty()) listOf(MediaSort.POPULARITY_DESC) else sort
        val checkFormatIn = if (formatIn.isNullOrEmpty()) null else formatIn
        val checkStatusIn = if (statusIn.isNullOrEmpty()) null else statusIn
        val checkSourceIn = if (sourceIn.isNullOrEmpty()) null else sourceIn
        val checkGenreIn = if (genreIn.isNullOrEmpty()) null else genreIn
        val checkGenreNotIn = if (genreNotIn.isNullOrEmpty()) null else genreNotIn
        val checkTagIn = if (tagIn.isNullOrEmpty()) null else tagIn
        val checkTagNotIn = if (tagNotIn.isNullOrEmpty()) null else tagNotIn
        val checkLicensedByIn = if (licensedByIn.isNullOrEmpty()) null else licensedByIn

        val query = SearchAnimeQuery(
            page = Input.fromNullable(page),
            search = Input.optional(checkSearch),
            sort = Input.optional(checkSort),
            formatIn = Input.optional(checkFormatIn),
            statusIn = Input.optional(checkStatusIn),
            sourceIn = Input.optional(checkSourceIn),
            countryOfOrigin = Input.optional(countryOfOrigin),
            season = Input.optional(season),
            startDateGreater = Input.optional(startDateGreater),
            startDateLesser = Input.optional(startDateLesser),
            isAdult = Input.optional(isAdult),
            onList = Input.optional(onList),
            genreIn = Input.optional(checkGenreIn),
            genreNotIn = Input.optional(checkGenreNotIn),
            minimumTagRank = Input.optional(minimumTagRank),
            tagIn = Input.optional(checkTagIn),
            tagNotIn = Input.optional(checkTagNotIn),
            licensedByIn = Input.optional(checkLicensedByIn),
            episodesGreater = Input.optional(episodesGreater),
            episodesLesser = Input.optional(episodesLesser),
            durationGreater = Input.optional(durationGreater),
            durationLesser = Input.optional(durationLesser),
            averageScoreGreater = Input.optional(averageScoreGreater),
            averageScoreLesser = Input.optional(averageScoreLesser),
            popularityGreater = Input.optional(popularityGreater),
            popularityLesser = Input.optional(popularityLesser)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchManga(
        page: Int,
        search: String?,
        sort: List<MediaSort>?,
        formatIn: List<MediaFormat>?,
        statusIn: List<MediaStatus>?,
        sourceIn: List<MediaSource>?,
        countryOfOrigin: String?,
        season: MediaSeason?,
        startDateGreater: Int?,
        startDateLesser: Int?,
        isAdult: Boolean?,
        onList: Boolean?,
        genreIn: List<String>?,
        genreNotIn: List<String>?,
        minimumTagRank: Int?,
        tagIn: List<String>?,
        tagNotIn: List<String>?,
        licensedByIn: List<String>?,
        chaptersGreater: Int?,
        chaptersLesser: Int?,
        volumesGreater: Int?,
        volumesLesser: Int?,
        averageScoreGreater: Int?,
        averageScoreLesser: Int?,
        popularityGreater: Int?,
        popularityLesser: Int?
    ): Observable<Response<SearchMangaQuery.Data>> {
        val checkSearch = if (search.isNullOrBlank()) null else search
        val checkSort = if (sort.isNullOrEmpty()) listOf(MediaSort.POPULARITY_DESC) else sort
        val checkFormatIn = if (formatIn.isNullOrEmpty()) null else formatIn
        val checkStatusIn = if (statusIn.isNullOrEmpty()) null else statusIn
        val checkSourceIn = if (sourceIn.isNullOrEmpty()) null else sourceIn
        val checkGenreIn = if (genreIn.isNullOrEmpty()) null else genreIn
        val checkGenreNotIn = if (genreNotIn.isNullOrEmpty()) null else genreNotIn
        val checkTagIn = if (tagIn.isNullOrEmpty()) null else tagIn
        val checkTagNotIn = if (tagNotIn.isNullOrEmpty()) null else tagNotIn
        val checkLicensedByIn = if (licensedByIn.isNullOrEmpty()) null else licensedByIn
        val query = SearchMangaQuery(
            page = Input.fromNullable(page),
            search = Input.optional(checkSearch),
            sort = Input.optional(checkSort),
            formatIn = Input.optional(checkFormatIn),
            statusIn = Input.optional(checkStatusIn),
            sourceIn = Input.optional(checkSourceIn),
            countryOfOrigin = Input.optional(countryOfOrigin),
            season = Input.optional(season),
            startDateGreater = Input.optional(startDateGreater),
            startDateLesser = Input.optional(startDateLesser),
            isAdult = Input.optional(isAdult),
            onList = Input.optional(onList),
            genreIn = Input.optional(checkGenreIn),
            genreNotIn = Input.optional(checkGenreNotIn),
            minimumTagRank = Input.optional(minimumTagRank),
            tagIn = Input.optional(checkTagIn),
            tagNotIn = Input.optional(checkTagNotIn),
            licensedByIn = Input.optional(checkLicensedByIn),
            chaptersGreater = Input.optional(chaptersGreater),
            chaptersLesser = Input.optional(chaptersLesser),
            volumesGreater = Input.optional(volumesGreater),
            volumesLesser = Input.optional(volumesLesser),
            averageScoreGreater = Input.optional(averageScoreGreater),
            averageScoreLesser = Input.optional(averageScoreLesser),
            popularityGreater = Input.optional(popularityGreater),
            popularityLesser = Input.optional(popularityLesser)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchCharacters(
        page: Int,
        search: String?,
        sort: List<CharacterSort>?
    ): Observable<Response<SearchCharactersQuery.Data>> {
        val checkSearch = if (search.isNullOrBlank()) null else search
        val checkSort = if (sort.isNullOrEmpty()) null else sort
        val query = SearchCharactersQuery(
            page = Input.fromNullable(page),
            search = Input.optional(checkSearch),
            sort = Input.optional(checkSort)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchStaffs(
        page: Int,
        search: String?,
        sort: List<StaffSort>?
    ): Observable<Response<SearchStaffsQuery.Data>> {
        val checkSearch = if (search.isNullOrBlank()) null else search
        val checkSort = if (sort.isNullOrEmpty()) null else sort
        val query = SearchStaffsQuery(
            page = Input.fromNullable(page),
            search = Input.optional(checkSearch),
            sort = Input.optional(checkSort)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchStudios(
        page: Int,
        search: String?,
        sort: List<StudioSort>?
    ): Observable<Response<SearchStudiosQuery.Data>> {
        val checkSearch = if (search.isNullOrBlank()) null else search
        val checkSort = if (sort.isNullOrEmpty()) null else sort
        val query = SearchStudiosQuery(
            page = Input.fromNullable(page),
            search = Input.optional(checkSearch),
            sort = Input.optional(checkSort)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchUsers(
        page: Int,
        search: String?,
        sort: List<UserSort>?
    ): Observable<Response<SearchUsersQuery.Data>> {
        val checkSearch = if (search.isNullOrBlank()) null else search
        val checkSort = if (sort.isNullOrEmpty()) null else sort
        val query = SearchUsersQuery(
            page = Input.fromNullable(page),
            search = Input.optional(checkSearch),
            sort = Input.optional(checkSort)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getSeasonalAnime(
        page: Int,
        season: MediaSeason?,
        seasonYear: Int?,
        status: MediaStatus?,
        formatIn: List<MediaFormat>,
        isAdult: Boolean,
        onList: Boolean?,
        sort: List<MediaSort>
    ): Observable<Response<SeasonalAnimeQuery.Data>> {
        val query = SeasonalAnimeQuery(
            page = Input.fromNullable(page),
            season = Input.fromNullable(season),
            seasonYear = Input.fromNullable(seasonYear),
            status = Input.optional(status),
            format_in = Input.fromNullable(formatIn),
            isAdult = Input.fromNullable(isAdult),
            onList = Input.optional(onList),
            sort = Input.fromNullable(sort)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchMediaImages(page:Int, idIn: List<Int>): Observable<Response<MediaImageQuery.Data>> {
        val query = MediaImageQuery(page = Input.fromNullable(page), id_in = Input.fromNullable(idIn))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchCharacterImages(
        page: Int,
        idIn: List<Int>
    ): Observable<Response<CharacterImageQuery.Data>> {
        val query = CharacterImageQuery(page = Input.fromNullable(page), id_in = Input.fromNullable(idIn))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getAiringSchedule(
        page: Int,
        airingAtGreater: Int,
        airingAtLesser: Int
    ): Observable<Response<AiringScheduleQuery.Data>> {
        val query = AiringScheduleQuery(
            page = Input.fromNullable(page),
            airingAtGreater = Input.fromNullable(airingAtGreater),
            airingAtLesser = Input.fromNullable(airingAtLesser)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}