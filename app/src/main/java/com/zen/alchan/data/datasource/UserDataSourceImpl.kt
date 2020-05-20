package com.zen.alchan.data.datasource

import AniListSettingsMutation
import FavoritesAnimeQuery
import FavoritesCharactersQuery
import FavoritesMangaQuery
import FavoritesStaffsQuery
import FavoritesStudiosQuery
import ListSettingsMutation
import ReorderFavoritesMutation
import ToggleFavouriteMutation
import UserFollowersQuery
import UserFollowingsQuery
import UserReviewsQuery
import UserStatisticsQuery
import ViewerQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.apollographql.apollo.rx2.rxPrefetch
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.helper.Constant
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.MediaListOptionsInput
import type.ScoreFormat
import type.UserTitleLanguage

class UserDataSourceImpl(private val apolloHandler: ApolloHandler) : UserDataSource {

    override fun getViewerData(): Observable<Response<ViewerQuery.Data>> {
        val query = ViewerQuery()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        adultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<Response<AniListSettingsMutation.Data>> {
        val mutation = AniListSettingsMutation(
            titleLanguage = Input.fromNullable(titleLanguage),
            displayAdultContent = Input.fromNullable(adultContent),
            airingNotifications = Input.fromNullable(airingNotifications)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Observable<Response<ListSettingsMutation.Data>> {
        val animeSectionOrder = ArrayList<String?>()

        if (animeListOptions.splitCompletedSectionByFormat == true) {
            animeSectionOrder.addAll(Constant.DEFAULT_SPLIT_ANIME_LIST_ORDER)
        } else {
            animeSectionOrder.addAll(Constant.DEFAULT_ANIME_LIST_ORDER)
        }

        if (!animeListOptions.customLists.isNullOrEmpty()) {
            animeSectionOrder.addAll(ArrayList(animeListOptions.customLists))
        }

        val animeListOptionsBuilder = MediaListOptionsInput(
            sectionOrder = Input.fromNullable(animeSectionOrder),
            splitCompletedSectionByFormat = Input.fromNullable(animeListOptions.splitCompletedSectionByFormat),
            customLists = Input.fromNullable(animeListOptions.customLists),
            advancedScoring = Input.fromNullable(animeListOptions.advancedScoring),
            advancedScoringEnabled = Input.fromNullable(animeListOptions.advancedScoringEnabled)
        )

        val mangaSectionOrder = ArrayList<String?>()

        if (mangaListOptions.splitCompletedSectionByFormat == true) {
            mangaSectionOrder.addAll(Constant.DEFAULT_SPLIT_MANGA_LIST_ORDER)
        } else {
            mangaSectionOrder.addAll(Constant.DEFAULT_MANGA_LIST_ORDER)
        }

        if (!mangaListOptions.customLists.isNullOrEmpty()) {
            mangaSectionOrder.addAll(ArrayList(mangaListOptions.customLists))
        }

        val mangaListOptionsBuilder = MediaListOptionsInput(
            sectionOrder = Input.fromNullable(mangaSectionOrder),
            splitCompletedSectionByFormat = Input.fromNullable(mangaListOptions.splitCompletedSectionByFormat),
            customLists = Input.fromNullable(mangaListOptions.customLists),
            advancedScoring = Input.fromNullable(mangaListOptions.advancedScoring),
            advancedScoringEnabled = Input.fromNullable(mangaListOptions.advancedScoringEnabled)
        )

        val mutation = ListSettingsMutation(
            scoreFormat = Input.fromNullable(scoreFormat),
            rowOrder = Input.fromNullable(rowOrder),
            animeListOptions = Input.fromNullable(animeListOptionsBuilder),
            mangaListOptions = Input.fromNullable(mangaListOptionsBuilder)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun toggleFavourite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ): Completable {
        val mutation = ToggleFavouriteMutation(
            animeId = Input.optional(animeId),
            mangaId = Input.optional(mangaId),
            characterId = Input.optional(characterId),
            staffId = Input.optional(staffId),
            studioId = Input.optional(studioId)
        )
        val mutationCall = apolloHandler.apolloClient.prefetch(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteAnime(page: Int): Observable<Response<FavoritesAnimeQuery.Data>> {
        val query = FavoritesAnimeQuery(page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteManga(page: Int): Observable<Response<FavoritesMangaQuery.Data>> {
        val query = FavoritesMangaQuery(page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteCharacters(page: Int): Observable<Response<FavoritesCharactersQuery.Data>> {
        val query = FavoritesCharactersQuery(page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteStaffs(page: Int): Observable<Response<FavoritesStaffsQuery.Data>> {
        val query = FavoritesStaffsQuery(page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteStudios(page: Int): Observable<Response<FavoritesStudiosQuery.Data>> {
        val query = FavoritesStudiosQuery(page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun reorderFavorites(
        animeIds: List<Int>?,
        mangaIds: List<Int>?,
        characterIds: List<Int>?,
        staffIds: List<Int>?,
        studioIds: List<Int>?,
        animeOrder: List<Int>?,
        mangaOrder: List<Int>?,
        characterOrder: List<Int>?,
        staffOrder: List<Int>?,
        studioOrder: List<Int>?
    ): Completable {
        val mutation = ReorderFavoritesMutation(
            animeIds = Input.optional(animeIds),
            mangaIds = Input.optional(mangaIds),
            characterIds = Input.optional(characterIds),
            staffIds = Input.optional(staffIds),
            studioIds = Input.optional(studioIds),
            animeOrder = Input.optional(animeOrder),
            mangaOrder = Input.optional(mangaOrder),
            characterOrder = Input.optional(characterOrder),
            staffOrder = Input.optional(staffOrder),
            studioOrder = Input.optional(studioOrder)
        )
        val mutationCall = apolloHandler.apolloClient.prefetch(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getReviews(userId: Int, page: Int): Observable<Response<UserReviewsQuery.Data>> {
        val query = UserReviewsQuery(
            userId = Input.fromNullable(userId),
            page = Input.fromNullable(page)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStatistics(userId: Int): Observable<Response<UserStatisticsQuery.Data>> {
        val query = UserStatisticsQuery(id = Input.fromNullable(userId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFollowers(
        userId: Int,
        page: Int
    ): Observable<Response<UserFollowersQuery.Data>> {
        val query = UserFollowersQuery(userId = userId, page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFollowings(
        userId: Int,
        page: Int
    ): Observable<Response<UserFollowingsQuery.Data>> {
        val query = UserFollowingsQuery(userId = userId, page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}