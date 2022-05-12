package com.zen.alchan.data.repository

import android.net.Uri
import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SaveItem
import com.zen.alchan.helper.utils.NotInStorageException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import type.ScoreFormat
import type.UserStaffNameLanguage
import type.UserStatisticsSort
import type.UserTitleLanguage

class DefaultUserRepository(
    private val userDataSource: UserDataSource,
    private val userManager: UserManager
) : UserRepository {

    private val _refreshMainScreenTrigger = PublishSubject.create<Unit>()
    override val refreshMainScreenTrigger: Observable<Unit>
        get() = _refreshMainScreenTrigger

    private val _refreshFavoriteTrigger = PublishSubject.create<User>()
    override val refreshFavoriteTrigger: Observable<User>
        get() = _refreshFavoriteTrigger

    override fun getIsLoggedInAsGuest(): Observable<Boolean> {
        return Observable.just(userManager.isLoggedInAsGuest)
    }

    override fun getIsAuthenticated(): Observable<Boolean> {
        return Observable.just(userManager.isAuthenticated)
    }

    override fun getViewer(source: Source?, sort: List<UserStatisticsSort>): Observable<User> {
        when (source) {
            Source.CACHE -> {
                return Observable.create { emitter ->
                    val savedViewer = userManager.viewerData?.data
                    if (savedViewer != null) {
                        emitter.onNext(savedViewer)
                        emitter.onComplete()
                    } else {
                        emitter.onError(NotInStorageException())
                    }
                }
            }
            else -> {
                return userDataSource.getViewerQuery(sort).map {
                    val newViewer = it.data?.convert()
                    if (newViewer != null) {
                        userManager.viewerData = SaveItem(newViewer)
                    }
                    newViewer ?: throw Throwable()
                }.onErrorReturn {
                    val savedViewer = userManager.viewerData?.data
                    savedViewer ?: throw NotInStorageException()
                }
            }
        }
    }

    override fun loginAsGuest() {
        userManager.isLoggedInAsGuest = true
    }

    override fun logoutAsGuest() {
        userManager.isLoggedInAsGuest = false
    }

    override fun logout() {
        userManager.bearerToken = null
        userManager.viewerData = null
        userManager.followingCount = null
        userManager.followersCount = null
    }

    override fun saveBearerToken(newBearerToken: String?) {
        userManager.bearerToken = newBearerToken
    }

    override fun getFollowingAndFollowersCount(userId: Int, source: Source?): Observable<Pair<Int, Int>> {
        when (source) {
            Source.CACHE -> {
                return Observable.create { emitter ->
                    val savedFollowingCount = userManager.followingCount ?: 0
                    val savedFollowersCount = userManager.followersCount ?: 0
                    emitter.onNext(savedFollowingCount to savedFollowersCount)
                    emitter.onComplete()
                }
            }
            else -> {
                return userDataSource.getFollowingAndFollowersCount(userId).map {
                    val newFollowingCount = it.data?.following?.pageInfo?.total ?: 0
                    val newFollowersCount = it.data?.followers?.pageInfo?.total ?: 0
                    userManager.followingCount = newFollowingCount
                    userManager.followersCount = newFollowersCount
                    newFollowingCount to newFollowersCount
                }.onErrorReturn {
                    val savedFollowingCount = userManager.followingCount ?: 0
                    val savedFollowersCount = userManager.followersCount ?: 0
                    savedFollowingCount to savedFollowersCount
                }
            }
        }
    }

    override fun getFollowing(userId: Int, page: Int): Observable<Pair<PageInfo, List<User>>> {
        return userDataSource.getFollowing(userId, page).map {
            it.data?.convert()
        }
    }

    override fun getFollowers(userId: Int, page: Int): Observable<Pair<PageInfo, List<User>>> {
        return userDataSource.getFollowers(userId, page).map {
            it.data?.convert()
        }
    }

    override fun toggleFollow(userId: Int): Observable<Boolean> {
        return userDataSource.toggleFollow(userId).toObservable().map {
            it.data?.toggleFollow?.isFollowing
        }
    }

    override fun getUserStatistics(
        userId: Int,
        sort: UserStatisticsSort
    ): Observable<UserStatisticTypes> {
        return userDataSource.getUserStatistics(userId, listOf(sort)).map {
            it.data?.convert()?.statistics
        }
    }

    override fun getFavorites(userId: Int, page: Int): Observable<Favourites> {
        return userDataSource.getFavorites(userId, page).map {
            it.data?.convert()
        }
    }

    override fun updateFavoriteOrder(ids: List<Int>, favorite: Favorite): Observable<Favourites> {
        return userDataSource.updateFavoriteOrder(ids, favorite).toObservable()
            .map {
                val newFavorites = it.data?.convert()
                if (newFavorites != null) {
                    val user = userManager.viewerData?.data
                    user?.let {
                        user.favourites = newFavorites
                        userManager.viewerData = SaveItem(user)
                        _refreshFavoriteTrigger.onNext(user)
                    }
                }
                newFavorites
            }
    }

    override fun toggleFavorite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ): Completable {
        return userDataSource.toggleFavorite(animeId, mangaId, characterId, staffId, studioId)
            .flatMapCompletable {
                getViewer(Source.NETWORK)
                    .flatMapCompletable {
                        _refreshFavoriteTrigger.onNext(it)
                        Completable.complete()
                    }
            }

    }

    override fun getAppSetting(): Observable<AppSetting> {
        return Observable.just(userManager.appSetting)
    }

    override fun setAppSetting(newAppSetting: AppSetting?): Observable<Unit> {
        return Observable.create {
            try {
                userManager.appSetting = newAppSetting ?: AppSetting()
                it.onNext(Unit)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    override fun getAppTheme(): AppTheme {
        return userManager.appSetting.appTheme
    }

    override fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<User> {
        return userDataSource.updateAniListSettings(
            titleLanguage,
            staffNameLanguage,
            activityMergeTime,
            displayAdultContent,
            airingNotifications
        )
            .toObservable()
            .doFinally {
                _refreshMainScreenTrigger.onNext(Unit)
            }
            .map {
                val newViewer = it.data?.convert()
                if (newViewer != null) {
                    userManager.viewerData = SaveItem(newViewer)
                }
                newViewer
            }
    }

    override fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Observable<User> {
        return userDataSource.updateListSettings(
            scoreFormat, rowOrder, animeListOptions, mangaListOptions
        )
            .toObservable()
            .doFinally {
                _refreshMainScreenTrigger.onNext(Unit)
            }
            .map {
                val newViewer = it.data?.convert()
                if (newViewer != null) {
                    userManager.viewerData = SaveItem(newViewer)
                }
                newViewer
            }
    }

    override fun updateNotificationsSettings(notificationOptions: List<NotificationOption>): Observable<User> {
        return userDataSource.updateNotificationsSettings(notificationOptions)
            .toObservable()
            .map {
                val newViewer = it.data?.convert()
                if (newViewer != null) {
                    userManager.viewerData = SaveItem(newViewer)
                }
                newViewer
            }
    }
}