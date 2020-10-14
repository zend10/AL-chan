package com.zen.alchan.data.datasource

import AniListSettingsMutation
import FavoritesAnimeQuery
import FavoritesCharactersQuery
import FavoritesMangaQuery
import FavoritesStaffsQuery
import FavoritesStudiosQuery
import ListSettingsMutation
import MediaListScoreCollectionQuery
import NotificationsQuery
import ReorderFavoritesMutation
import SessionQuery
import ToggleFavouriteMutation
import ToggleFollowMutation
import UserFollowersQuery
import UserFollowingsQuery
import UserMediaListCollectionQuery
import UserQuery
import UserReviewsQuery
import UserStatisticsQuery
import ViewerNotificationCountQuery
import ViewerQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.apollographql.apollo.rx2.rxPrefetch
import com.google.firebase.database.FirebaseDatabase
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.data.response.NotificationOption
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.pojo.FirebaseUser
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.*

class UserDataSourceImpl(private val apolloHandler: ApolloHandler) : UserDataSource {

    override fun checkSession(): Observable<Response<SessionQuery.Data>> {
        val query = SessionQuery()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

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
        val animeListOptionsBuilder = MediaListOptionsInput(
            sectionOrder = Input.fromNullable(animeListOptions.sectionOrder),
            splitCompletedSectionByFormat = Input.fromNullable(animeListOptions.splitCompletedSectionByFormat),
            customLists = Input.fromNullable(animeListOptions.customLists),
            advancedScoring = Input.fromNullable(animeListOptions.advancedScoring),
            advancedScoringEnabled = Input.fromNullable(animeListOptions.advancedScoringEnabled)
        )

        val mangaListOptionsBuilder = MediaListOptionsInput(
            sectionOrder = Input.fromNullable(mangaListOptions.sectionOrder),
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

    override fun updateNotificationsSettings(notificationOptions: List<NotificationOption>): Observable<Response<AniListSettingsMutation.Data>> {
        val inputList = ArrayList<NotificationOptionInput>()
        notificationOptions.forEach {
            val input = NotificationOptionInput(type = Input.fromNullable(it.type), enabled = Input.fromNullable(it.enabled))
            inputList.add(input)
        }
        val mutation = AniListSettingsMutation(
            notificationOptions = Input.fromNullable(inputList)
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

    override fun getFavoriteAnime(id: Int, page: Int): Observable<Response<FavoritesAnimeQuery.Data>> {
        val query = FavoritesAnimeQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteManga(id: Int, page: Int): Observable<Response<FavoritesMangaQuery.Data>> {
        val query = FavoritesMangaQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteCharacters(id: Int, page: Int): Observable<Response<FavoritesCharactersQuery.Data>> {
        val query = FavoritesCharactersQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteStaffs(id: Int, page: Int): Observable<Response<FavoritesStaffsQuery.Data>> {
        val query = FavoritesStaffsQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavoriteStudios(id: Int, page: Int): Observable<Response<FavoritesStudiosQuery.Data>> {
        val query = FavoritesStudiosQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
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

    override fun toggleFollow(userId: Int): Observable<Response<ToggleFollowMutation.Data>> {
        val mutation = ToggleFollowMutation(userId = Input.fromNullable(userId))
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUserData(id: Int): Observable<Response<UserQuery.Data>> {
        val query = UserQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUserMediaCollection(
        userId: Int,
        type: MediaType
    ): Observable<Response<UserMediaListCollectionQuery.Data>> {
        val query = UserMediaListCollectionQuery(userId = Input.fromNullable(userId), type = Input.fromNullable(type))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getNotification(page: Int, typeIn: List<NotificationType>?, reset: Boolean): Observable<Response<NotificationsQuery.Data>> {
        val query = NotificationsQuery(page = Input.fromNullable(page), type_in = Input.optional(typeIn), reset = Input.optional(reset))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getNotificationCount(): Observable<Response<ViewerNotificationCountQuery.Data>> {
        val query = ViewerNotificationCountQuery()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUserScores(
        currentUserId: Int,
        otherUserId: Int,
        type: MediaType
    ): Observable<Response<MediaListScoreCollectionQuery.Data>> {
        val query = MediaListScoreCollectionQuery(
            userId = Input.fromNullable(currentUserId),
            otherUserId = Input.fromNullable(otherUserId),
            type = Input.fromNullable(type)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun sendFirebaseToken(userId: Int, name: String, token: String) {
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(userId.toString()).setValue(FirebaseUser(name, token))
    }
}