package com.zen.alchan.data.datasource

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
import type.*

class SearchDataSourceImpl(private val apolloHandler: ApolloHandler) : SearchDataSource {

    override fun searchAnime(
        page: Int,
        search: String?,
        season: MediaSeason?,
        seasonYear: Int?,
        format: MediaFormat?,
        status: MediaStatus?,
        isAdult: Boolean?,
        onList: Boolean?,
        source: MediaSource?,
        countryOfOrigin: String?,
        genreIn: List<String?>?,
        tagIn: List<String?>?,
        sort: List<MediaSort>?
    ): Observable<Response<SearchAnimeQuery.Data>> {
        val checkSearch = if (search.isNullOrBlank()) null else search
        val checkGenre = if (genreIn.isNullOrEmpty()) null else genreIn
        val checkTag = if (tagIn.isNullOrEmpty()) null else tagIn
        val checkSort = if (sort.isNullOrEmpty()) null else sort
        val query = SearchAnimeQuery(
            page = Input.fromNullable(page),
            search = Input.optional(checkSearch),
            season = Input.optional(season),
            seasonYear = Input.optional(seasonYear),
            format = Input.optional(format),
            status = Input.optional(status),
            isAdult = Input.optional(isAdult),
            onList = Input.optional(onList),
            source = Input.optional(source),
            countryOfOrigin = Input.optional(countryOfOrigin),
            genre_in = Input.optional(checkGenre),
            tag_in = Input.optional(checkTag),
            sort = Input.optional(checkSort)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchManga(
        page: Int,
        search: String?,
        startDateGreater: Int?,
        endDateLesser: Int?,
        format: MediaFormat?,
        status: MediaStatus?,
        isAdult: Boolean?,
        onList: Boolean?,
        source: MediaSource?,
        countryOfOrigin: String?,
        genreIn: List<String?>?,
        tagIn: List<String?>?,
        sort: List<MediaSort>?
    ): Observable<Response<SearchMangaQuery.Data>> {
        val checkSearch = if (search.isNullOrBlank()) null else search
        val checkGenre = if (genreIn.isNullOrEmpty()) null else genreIn
        val checkTag = if (tagIn.isNullOrEmpty()) null else tagIn
        val checkSort = if (sort.isNullOrEmpty()) null else sort
        val query = SearchMangaQuery(
            page = Input.fromNullable(page),
            search = Input.optional(checkSearch),
            startDate_greater = Input.optional(startDateGreater),
            endDate_lesser = Input.optional(endDateLesser),
            format = Input.optional(format),
            status = Input.optional(status),
            isAdult = Input.optional(isAdult),
            onList = Input.optional(onList),
            source = Input.optional(source),
            countryOfOrigin = Input.optional(countryOfOrigin),
            genre_in = Input.optional(checkGenre),
            tag_in = Input.optional(checkTag),
            sort = Input.optional(checkSort)
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
}