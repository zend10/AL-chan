package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.data.response.NotificationOption
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.ScoreFormat
import type.UserTitleLanguage

class UserRepositoryImpl(private val userDataSource: UserDataSource,
                         private val userManager: UserManager
) : UserRepository {

    private val _sessionResponse = SingleLiveEvent<Boolean>()
    override val sessionResponse: LiveData<Boolean>
        get() = _sessionResponse

    private val _viewerDataResponse = MutableLiveData<Resource<Boolean>>()
    override val viewerDataResponse: LiveData<Resource<Boolean>>
        get() = _viewerDataResponse

    private val _viewerData = MutableLiveData<User?>()
    override val viewerData: LiveData<User?>
        get() = _viewerData

    private val _listOrAniListSettingsChanged = SingleLiveEvent<Boolean>()
    override val listOrAniListSettingsChanged: LiveData<Boolean>
        get() = _listOrAniListSettingsChanged

    private val _followersCount = MutableLiveData<Int>()
    override val followersCount: LiveData<Int>
        get() = _followersCount

    private val _followingsCount = MutableLiveData<Int>()
    override val followingsCount: LiveData<Int>
        get() = _followingsCount

    private val _updateAniListSettingsResponse = SingleLiveEvent<Resource<Boolean>>()
    override val updateAniListSettingsResponse: LiveData<Resource<Boolean>>
        get() = _updateAniListSettingsResponse

    private val _updateListSettingsResponse = SingleLiveEvent<Resource<Boolean>>()
    override val updateListSettingsResponse: LiveData<Resource<Boolean>>
        get() = _updateListSettingsResponse

    private val _toggleFavouriteResponse = SingleLiveEvent<Resource<Boolean>>()
    override val toggleFavouriteResponse: LiveData<Resource<Boolean>>
        get() = _toggleFavouriteResponse

    private val _favoriteAnimeResponse = SingleLiveEvent<Resource<FavoritesAnimeQuery.Data>>()
    override val favoriteAnimeResponse: LiveData<Resource<FavoritesAnimeQuery.Data>>
        get() = _favoriteAnimeResponse

    private val _favoriteMangaResponse = SingleLiveEvent<Resource<FavoritesMangaQuery.Data>>()
    override val favoriteMangaResponse: LiveData<Resource<FavoritesMangaQuery.Data>>
        get() = _favoriteMangaResponse

    private val _favoriteCharactersResponse = SingleLiveEvent<Resource<FavoritesCharactersQuery.Data>>()
    override val favoriteCharactersResponse: LiveData<Resource<FavoritesCharactersQuery.Data>>
        get() = _favoriteCharactersResponse

    private val _favoriteStaffsResponse = SingleLiveEvent<Resource<FavoritesStaffsQuery.Data>>()
    override val favoriteStaffsResponse: LiveData<Resource<FavoritesStaffsQuery.Data>>
        get() = _favoriteStaffsResponse

    private val _favoriteStudiosResponse = SingleLiveEvent<Resource<FavoritesStudiosQuery.Data>>()
    override val favoriteStudiosResponse: LiveData<Resource<FavoritesStudiosQuery.Data>>
        get() = _favoriteStudiosResponse

    private val _viewerReviewsResponse = SingleLiveEvent<Resource<UserReviewsQuery.Data>>()
    override val viewerReviewsResponse: LiveData<Resource<UserReviewsQuery.Data>>
        get() = _viewerReviewsResponse

    private val _userStatisticsResponse = SingleLiveEvent<Resource<UserStatisticsQuery.Data>>()
    override val userStatisticsResponse: LiveData<Resource<UserStatisticsQuery.Data>>
        get() = _userStatisticsResponse

    private val _triggerRefreshFavorite = SingleLiveEvent<Boolean>()
    override val triggerRefreshFavorite: LiveData<Boolean>
        get() = _triggerRefreshFavorite

    private val _triggerRefreshReviews = SingleLiveEvent<Boolean>()
    override val triggerRefreshReviews: LiveData<Boolean>
        get() = _triggerRefreshReviews

    private val _reorderFavoritesResponse = SingleLiveEvent<Resource<Boolean>>()
    override val reorderFavoritesResponse: LiveData<Resource<Boolean>>
        get() = _reorderFavoritesResponse

    override val viewerDataLastRetrieved: Long?
        get() = userManager.viewerDataLastRetrieved

    override val followersCountLastRetrieved: Long?
        get() = userManager.followersCountLastRetrieved

    override val followingsCountLastRetrieved: Long?
        get() = userManager.followingsCountLastRetrieved

    @SuppressLint("CheckResult")
    override fun checkSession() {
        userDataSource.checkSession().subscribeWith(object : Observer<Response<SessionQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<SessionQuery.Data>) { }

            override fun onError(e: Throwable) {
                if (e is ApolloHttpException) {
                    if (e.rawResponse()?.code == 401 || e.rawResponse()?.code == 400) {
                        _sessionResponse.postValue(false)
                    }
                }
            }

            override fun onComplete() { }
        })
    }

    override fun getViewerData() {
        // used to trigger live data
        _viewerData.postValue(userManager.viewerData)
        _followersCount.postValue(userManager.followersCount)
        _followingsCount.postValue(userManager.followingsCount)
    }

    @SuppressLint("CheckResult")
    override fun retrieveViewerData() {
        _viewerDataResponse.postValue(Resource.Loading())

        userDataSource.getViewerData().subscribeWith(object : Observer<Response<ViewerQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<ViewerQuery.Data>) {
                if (t.hasErrors()) {
                    _viewerDataResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    userManager.setViewerData(Converter.convertUser(t.data?.viewer))
                    _viewerDataResponse.postValue(Resource.Success(true))
                    _viewerData.postValue(userManager.viewerData)
                }
            }

            override fun onError(e: Throwable) {
                _viewerDataResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        adultContent: Boolean,
        airingNotifications: Boolean
    ) {
        _updateAniListSettingsResponse.postValue(Resource.Loading())

        userDataSource.updateAniListSettings(titleLanguage, adultContent, airingNotifications).subscribeWith(object : Observer<Response<AniListSettingsMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AniListSettingsMutation.Data>) {
                if (t.hasErrors()) {
                    _updateAniListSettingsResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    val savedUser = userManager.viewerData
                    savedUser?.options = Converter.convertUserOptions(t.data?.updateUser?.options)
                    userManager.setViewerData(savedUser)

                    _updateAniListSettingsResponse.postValue(Resource.Success(true))
                    _viewerData.postValue(userManager.viewerData)
                    _listOrAniListSettingsChanged.postValue(true)
                }
            }

            override fun onError(e: Throwable) {
                _updateAniListSettingsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ) {
        _updateListSettingsResponse.postValue(Resource.Loading())

        userDataSource.updateListSettings(scoreFormat, rowOrder, animeListOptions, mangaListOptions).subscribeWith(object : Observer<Response<ListSettingsMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<ListSettingsMutation.Data>) {
                if (t.hasErrors()) {
                    _updateListSettingsResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    val savedUser = userManager.viewerData
                    savedUser?.mediaListOptions = Converter.convertMediaListOptions(t.data?.updateUser?.mediaListOptions)
                    userManager.setViewerData(savedUser)

                    _updateListSettingsResponse.postValue(Resource.Success(true))
                    _viewerData.postValue(userManager.viewerData)
                    _listOrAniListSettingsChanged.postValue(true)
                }
            }

            override fun onError(e: Throwable) {
                _updateListSettingsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun updateNotificationsSettings(notificationOptions: List<NotificationOption>) {
        _updateAniListSettingsResponse.postValue(Resource.Loading())

        userDataSource.updateNotificationsSettings(notificationOptions).subscribeWith(object : Observer<Response<AniListSettingsMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AniListSettingsMutation.Data>) {
                if (t.hasErrors()) {
                    _updateAniListSettingsResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    val savedUser = userManager.viewerData
                    savedUser?.options = Converter.convertUserOptions(t.data?.updateUser?.options)
                    userManager.setViewerData(savedUser)

                    _updateAniListSettingsResponse.postValue(Resource.Success(true))
                    _viewerData.postValue(userManager.viewerData)
                }
            }

            override fun onError(e: Throwable) {
                _updateAniListSettingsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun toggleFavourite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ) {
        _toggleFavouriteResponse.postValue(Resource.Loading())

        userDataSource.toggleFavourite(animeId, mangaId, characterId, staffId, studioId).subscribeWith(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) { }

            override fun onError(e: Throwable) {
                _toggleFavouriteResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {
                _toggleFavouriteResponse.postValue(Resource.Success(true))
            }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFavoriteAnime(page: Int) {
        _favoriteAnimeResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteAnime(page).subscribeWith(object : Observer<Response<FavoritesAnimeQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<FavoritesAnimeQuery.Data>) {
                if (t.hasErrors()) {
                    _favoriteAnimeResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _favoriteAnimeResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _favoriteAnimeResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFavoriteManga(page: Int) {
        _favoriteMangaResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteManga(page).subscribeWith(object : Observer<Response<FavoritesMangaQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<FavoritesMangaQuery.Data>) {
                if (t.hasErrors()) {
                    _favoriteMangaResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _favoriteMangaResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _favoriteMangaResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFavoriteCharacters(page: Int) {
        _favoriteCharactersResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteCharacters(page).subscribeWith(object : Observer<Response<FavoritesCharactersQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<FavoritesCharactersQuery.Data>) {
                if (t.hasErrors()) {
                    _favoriteCharactersResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _favoriteCharactersResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _favoriteCharactersResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFavoriteStaffs(page: Int) {
        _favoriteStaffsResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteStaffs(page).subscribeWith(object : Observer<Response<FavoritesStaffsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<FavoritesStaffsQuery.Data>) {
                if (t.hasErrors()) {
                    _favoriteStaffsResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _favoriteStaffsResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _favoriteStaffsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFavoriteStudios(page: Int) {
        _favoriteStudiosResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteStudios(page).subscribeWith(object : Observer<Response<FavoritesStudiosQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<FavoritesStudiosQuery.Data>) {
                if (t.hasErrors()) {
                    _favoriteStudiosResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _favoriteStudiosResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _favoriteStudiosResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    override fun triggerRefreshProfilePageChild() {
        _triggerRefreshFavorite.postValue(true)
        _triggerRefreshReviews.postValue(true)
        getStatistics()
    }

    @SuppressLint("CheckResult")
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
    ) {
        _reorderFavoritesResponse.postValue(Resource.Loading())

        userDataSource.reorderFavorites(animeIds, mangaIds, characterIds, staffIds, studioIds, animeOrder, mangaOrder, characterOrder, staffOrder, studioOrder).subscribeWith(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) { }

            override fun onError(e: Throwable) {
                _reorderFavoritesResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {
                _reorderFavoritesResponse.postValue(Resource.Success(true))
                _triggerRefreshFavorite.postValue(true)
            }
        })
    }

    @SuppressLint("CheckResult")
    override fun getReviews(page: Int) {
        if (userManager.viewerData?.id == null) {
            return
        }

        _viewerReviewsResponse.postValue(Resource.Loading())

        userDataSource.getReviews(userManager.viewerData?.id!!, page).subscribeWith(object : Observer<Response<UserReviewsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserReviewsQuery.Data>) {
                if (t.hasErrors()) {
                    _viewerReviewsResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _viewerReviewsResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _viewerReviewsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getStatistics() {
        if (userManager.viewerData?.id == null) {
            return
        }

        _userStatisticsResponse.postValue(Resource.Loading())

        userDataSource.getStatistics(userManager.viewerData?.id!!).subscribeWith(object : Observer<Response<UserStatisticsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsQuery.Data>) {
                if (t.hasErrors()) {
                    _userStatisticsResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _userStatisticsResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _userStatisticsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFollowersCount() {
        if (userManager.viewerData?.id == null) {
            return
        }

        userDataSource.getFollowers(userManager.viewerData?.id!!, 1).subscribeWith(object : Observer<Response<UserFollowersQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserFollowersQuery.Data>) {
                if (!t.hasErrors()) {
                    userManager.setFollowersCount(t.data?.page?.pageInfo?.total ?: 0)
                    _followersCount.postValue(userManager.followersCount)
                }
            }

            override fun onError(e: Throwable) { }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFollowingsCount() {
        if (userManager.viewerData?.id == null) {
            return
        }

        userDataSource.getFollowings(userManager.viewerData?.id!!, 1).subscribeWith(object : Observer<Response<UserFollowingsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserFollowingsQuery.Data>) {
                if (!t.hasErrors()) {
                    userManager.setFollowingsCount(t.data?.page?.pageInfo?.total ?: 0)
                    _followingsCount.postValue(userManager.followingsCount)
                }
            }

            override fun onError(e: Throwable) { }

            override fun onComplete() { }
        })
    }
}