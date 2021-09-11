package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.MediaListTypeOptions
import com.zen.alchan.data.response.anilist.NotificationOption
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.moreThanADay
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.SaveItem
import com.zen.alchan.helper.utils.NotInStorageException
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import type.ScoreFormat
import type.UserStaffNameLanguage
import type.UserStatisticsSort
import type.UserTitleLanguage

class DefaultUserRepository(
    private val userDataSource: UserDataSource,
    private val userManager: UserManager
) : UserRepository {

    private val _animeListStyle = BehaviorSubject.createDefault(userManager.animeListStyle)
    private val animeListStyle: Observable<ListStyle>
        get() = _animeListStyle

    private val _mangaListStyle = BehaviorSubject.createDefault(userManager.mangaListStyle)
    private val mangaListStyle: Observable<ListStyle>
        get() = _mangaListStyle

    override fun getIsLoggedInAsGuest(): Observable<Boolean> {
        return Observable.just(userManager.isLoggedInAsGuest)
    }

    override fun getIsAuthenticated(): Observable<Boolean> {
        return Observable.just(userManager.isAuthenticated)
    }

    override fun getViewer(source: Source?): Observable<User> {
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
                return Observable.create { emitter ->
                    userDataSource.getViewerQuery().subscribe(
                        {
                            val newViewer = it.data?.convert()

                            if (newViewer != null) {
                                userManager.viewerData = SaveItem(newViewer)
                                emitter.onNext(newViewer)
                                emitter.onComplete()
                            } else {
                                throw Throwable()
                            }
                        },
                        {
                            val savedViewer = userManager.viewerData?.data

                            if (savedViewer != null) {
                                emitter.onNext(savedViewer)
                                emitter.onComplete()
                            } else {
                                emitter.onError(NotInStorageException())
                            }
                        }
                    )
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
        userManager.profileData = null
    }

    override fun saveBearerToken(newBearerToken: String?) {
        userManager.bearerToken = newBearerToken
    }

    override fun getProfileData(userId: Int, sort: List<UserStatisticsSort>, source: Source?): Observable<ProfileData> {
        if (userManager.viewerData?.data?.id != userId)
            return getProfileDataFromNetwork(userId, sort)

        return when (source) {
            Source.NETWORK -> getProfileDataFromNetwork(userId, sort)
            Source.CACHE -> getProfileDataFromCache()
            else -> {
                val savedItem = userManager.profileData
                if (savedItem == null || savedItem.saveTime.moreThanADay()) {
                    getProfileDataFromNetwork(userId, sort)
                } else {
                    Observable.just(savedItem.data)
                }
            }
        }
    }

    private fun getProfileDataFromNetwork(userId: Int, sort: List<UserStatisticsSort>): Observable<ProfileData> {
        return userDataSource.getProfileQuery(userId, sort).map {
            val newProfileData = it.data?.convert() ?: ProfileData()

            if (userManager.viewerData?.data?.id == newProfileData.user.id)
                userManager.profileData = SaveItem(newProfileData)

            newProfileData
        }
    }

    private fun getProfileDataFromCache(): Observable<ProfileData> {
        return Observable.create {
            val savedItem = userManager.profileData?.data
            if (savedItem != null) {
                it.onNext(savedItem)
                it.onComplete()
            } else {
                it.onError(NotInStorageException())
            }
        }
    }

    override fun getListStyle(mediaType: MediaType): Observable<ListStyle> {
        return when (mediaType) {
            MediaType.ANIME -> animeListStyle
            MediaType.MANGA -> mangaListStyle
        }
    }

    override fun setListStyle(mediaType: MediaType, newListStyle: ListStyle) {
        when (mediaType) {
            MediaType.ANIME -> {
                userManager.animeListStyle = newListStyle
                _animeListStyle.onNext(newListStyle)
            }
            MediaType.MANGA -> {
                userManager.mangaListStyle = newListStyle
                _mangaListStyle.onNext(newListStyle)
            }
        }
    }

    override fun getMediaFilter(mediaType: MediaType): Observable<MediaFilter> {
        return Observable.just(
            when (mediaType) {
                MediaType.ANIME -> userManager.animeFilter
                MediaType.MANGA -> userManager.mangaFilter
            }
        )
    }

    override fun setMediaFilter(mediaType: MediaType, newMediaFilter: MediaFilter) {
        when (mediaType) {
            MediaType.ANIME -> userManager.animeFilter = newMediaFilter
            MediaType.MANGA -> userManager.mangaFilter = newMediaFilter
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