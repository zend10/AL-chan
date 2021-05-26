package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.moreThanADay
import com.zen.alchan.helper.pojo.SaveItem
import com.zen.alchan.helper.utils.StorageException
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import type.UserStatisticsSort

class DefaultUserRepository(
    private val userDataSource: UserDataSource,
    private val userManager: UserManager
) : UserRepository {

    private val _viewer = PublishSubject.create<User>()
    override val viewer: Observable<User>
        get() = _viewer

    private val _appSetting = PublishSubject.create<AppSetting>()
    override val appSetting: Observable<AppSetting>
        get() = _appSetting

    private var viewerDisposable: Disposable? = null

    override fun getIsLoggedIn(): Observable<Boolean> {
        return Observable.just(userManager.isLoggedInAsGuest)
    }

    override fun getIsAuthenticated(): Observable<Boolean> {
        return Observable.just(userManager.isAuthenticated)
    }

    override fun loadViewer(source: Source?) {
        when (source) {
            Source.CACHE -> {
                val savedViewer = userManager.viewerData?.data
                _viewer.onNext(savedViewer ?: User.EMPTY_USER)
            }
            else -> {
                if (viewerDisposable?.isDisposed == false)
                    return

                viewerDisposable = userDataSource.getViewerQuery()
                    .subscribe(
                        {
                            val newViewer = it.data?.convert()

                            if (newViewer != null)
                                userManager.viewerData = SaveItem(newViewer)

                            _viewer.onNext(newViewer ?: User.EMPTY_USER)

                            viewerDisposable?.dispose()
                        },
                        {
                            val savedViewer = userManager.viewerData?.data
                            _viewer.onNext(savedViewer ?: User.EMPTY_USER)

                            viewerDisposable?.dispose()
                        }
                    )
            }
        }
    }

    override fun loginAsGuest() {
        userManager.isLoggedInAsGuest = true
    }

    override fun logout() {
        userManager.isLoggedInAsGuest = false
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
                it.onError(StorageException())
            }
        }
    }

    override fun loadAppSetting() {
        _appSetting.onNext(userManager.appSetting)
    }

    override fun setAppSetting(newAppSetting: AppSetting?) {
        userManager.appSetting = newAppSetting ?: AppSetting.EMPTY_APP_SETTING
    }

    override fun getAppTheme(): AppTheme {
        return userManager.appSetting.appTheme
    }
}