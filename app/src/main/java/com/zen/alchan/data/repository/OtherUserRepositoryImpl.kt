package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.MediaType

class OtherUserRepositoryImpl(private val userDataSource: UserDataSource) : OtherUserRepository {

    private val _userDataResponse = MutableLiveData<Resource<Boolean>>()
    override val userDataResponse: LiveData<Resource<Boolean>>
        get() = _userDataResponse

    private val _userData = MutableLiveData<UserQuery.Data>()
    override val userData: LiveData<UserQuery.Data>
        get() = _userData

    private val _followersCount = MutableLiveData<Int>()
    override val followersCount: LiveData<Int>
        get() = _followersCount

    private val _followingsCount = MutableLiveData<Int>()
    override val followingsCount: LiveData<Int>
        get() = _followingsCount

    private val _userMediaListCollection = SingleLiveEvent<Resource<UserMediaListCollectionQuery.Data>>()
    override val userMediaListCollection: LiveData<Resource<UserMediaListCollectionQuery.Data>>
        get() = _userMediaListCollection

    private val _userFollowersResponse = SingleLiveEvent<Resource<UserFollowersQuery.Data>>()
    override val userFollowersResponse: LiveData<Resource<UserFollowersQuery.Data>>
        get() = _userFollowersResponse

    private val _userFollowingsResponse = SingleLiveEvent<Resource<UserFollowingsQuery.Data>>()
    override val userFollowingsResponse: LiveData<Resource<UserFollowingsQuery.Data>>
        get() = _userFollowingsResponse

    private val _triggerRefreshFavorite = SingleLiveEvent<Boolean>()
    override val triggerRefreshFavorite: LiveData<Boolean>
        get() = _triggerRefreshFavorite

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

    private val _userStatisticsResponse = SingleLiveEvent<Resource<UserStatisticsQuery.Data>>()
    override val userStatisticsResponse: LiveData<Resource<UserStatisticsQuery.Data>>
        get() = _userStatisticsResponse

    private val _triggerRefreshReviews = SingleLiveEvent<Boolean>()
    override val triggerRefreshReviews: LiveData<Boolean>
        get() = _triggerRefreshReviews

    private val _userReviewsResponse = SingleLiveEvent<Resource<UserReviewsQuery.Data>>()
    override val userReviewsResponse: LiveData<Resource<UserReviewsQuery.Data>>
        get() = _userReviewsResponse

    @SuppressLint("CheckResult")
    override fun retrieveUserData(userId: Int) {
        _userDataResponse.postValue(Resource.Loading())

        userDataSource.getUserData(userId).subscribeWith(object : Observer<Response<UserQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserQuery.Data>) {
                if (t.hasErrors()) {
                    _userDataResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _userDataResponse.postValue(Resource.Success(true))
                    _userData.postValue(t.data)
                }
            }

            override fun onError(e: Throwable) {
                _userDataResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFollowersCount(userId: Int) {
        userDataSource.getFollowers(userId, 1).subscribeWith(object : Observer<Response<UserFollowersQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserFollowersQuery.Data>) {
                if (!t.hasErrors()) {
                    _followersCount.postValue(t.data?.page?.pageInfo?.total ?: 0)
                }
            }

            override fun onError(e: Throwable) { }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getFollowingsCount(userId: Int) {
        userDataSource.getFollowings(userId, 1).subscribeWith(object : Observer<Response<UserFollowingsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserFollowingsQuery.Data>) {
                if (!t.hasErrors()) {
                    _followingsCount.postValue(t.data?.page?.pageInfo?.total ?: 0)
                }
            }

            override fun onError(e: Throwable) { }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getUserMediaListCollection(userId: Int, type: MediaType) {
        _userMediaListCollection.postValue(Resource.Loading())

        userDataSource.getUserMediaCollection(userId, type).subscribeWith(object : Observer<Response<UserMediaListCollectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserMediaListCollectionQuery.Data>) {
                if (t.hasErrors()) {
                    _userMediaListCollection.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _userMediaListCollection.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _userMediaListCollection.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getUserFollowers(userId: Int, page: Int) {
        userDataSource.getFollowers(userId, page).subscribeWith(object : Observer<Response<UserFollowersQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserFollowersQuery.Data>) {
                if (!t.hasErrors()) {
                    _userFollowersResponse.postValue(Resource.Success(t.data!!))
                } else {
                    _userFollowersResponse.postValue(Resource.Error(t.errors!![0].message))
                }
            }

            override fun onError(e: Throwable) {
                _userFollowersResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun getUserFollowings(userId: Int, page: Int) {
        userDataSource.getFollowings(userId, page).subscribeWith(object : Observer<Response<UserFollowingsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserFollowingsQuery.Data>) {
                if (!t.hasErrors()) {
                    _userFollowingsResponse.postValue(Resource.Success(t.data!!))
                } else {
                    _userFollowingsResponse.postValue(Resource.Error(t.errors!![0].message))
                }
            }

            override fun onError(e: Throwable) {
                _userFollowingsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    override fun triggerRefreshProfilePageChild(userId: Int) {
        _triggerRefreshFavorite.postValue(true)
        _triggerRefreshReviews.postValue(true)
        getStatistics(userId)
    }

    @SuppressLint("CheckResult")
    override fun getFavoriteAnime(userId: Int, page: Int) {
        _favoriteAnimeResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteAnime(userId, page).subscribeWith(object : Observer<Response<FavoritesAnimeQuery.Data>> {
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
    override fun getFavoriteManga(userId: Int, page: Int) {
        _favoriteMangaResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteManga(userId, page).subscribeWith(object : Observer<Response<FavoritesMangaQuery.Data>> {
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
    override fun getFavoriteCharacters(userId: Int, page: Int) {
        _favoriteCharactersResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteCharacters(userId, page).subscribeWith(object : Observer<Response<FavoritesCharactersQuery.Data>> {
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
    override fun getFavoriteStaffs(userId: Int, page: Int) {
        _favoriteStaffsResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteStaffs(userId, page).subscribeWith(object : Observer<Response<FavoritesStaffsQuery.Data>> {
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
    override fun getFavoriteStudios(userId: Int, page: Int) {
        _favoriteStudiosResponse.postValue(Resource.Loading())

        userDataSource.getFavoriteStudios(userId, page).subscribeWith(object : Observer<Response<FavoritesStudiosQuery.Data>> {
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

    @SuppressLint("CheckResult")
    override fun getStatistics(userId: Int) {
        _userStatisticsResponse.postValue(Resource.Loading())

        userDataSource.getStatistics(userId).subscribeWith(object : Observer<Response<UserStatisticsQuery.Data>> {
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
    override fun getReviews(userId: Int, page: Int) {
        _userReviewsResponse.postValue(Resource.Loading())

        userDataSource.getReviews(userId, page).subscribeWith(object : Observer<Response<UserReviewsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserReviewsQuery.Data>) {
                if (t.hasErrors()) {
                    _userReviewsResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _userReviewsResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _userReviewsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }
}