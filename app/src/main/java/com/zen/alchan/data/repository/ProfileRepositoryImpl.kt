package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.localstorage.AppSettingsManager
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.ScoreFormat
import type.UserTitleLanguage

class ProfileRepositoryImpl(private val userDataSource: UserDataSource,
                            private val appSettingsManager: AppSettingsManager,
                            private val userManager: UserManager
) : ProfileRepository {

    private val _viewerDataResponse = SingleLiveEvent<Resource<Boolean>>()
    override val viewerDataResponse: LiveData<Resource<Boolean>>
        get() = _viewerDataResponse

    private val _viewerData = MutableLiveData<User?>()
    override val viewerData: LiveData<User?>
        get() = _viewerData

    private val _updateAniListSettingsResponse = SingleLiveEvent<Resource<Boolean>>()
    override val updateAniListSettingsResponse: LiveData<Resource<Boolean>>
        get() = _updateAniListSettingsResponse

    private val _updateListSettingsResponse = SingleLiveEvent<Resource<Boolean>>()
    override val updateListSettingsResponse: LiveData<Resource<Boolean>>
        get() = _updateListSettingsResponse

    override val appColorTheme: AppColorTheme
        get() = appSettingsManager.appColorTheme

    override val homeShowWatching: Boolean
        get() = appSettingsManager.homeShowWatching

    override val homeShowReading: Boolean
        get() = appSettingsManager.homeShowReading

    override val pushNotificationsSettings: PushNotificationsSettings
        get() = appSettingsManager.pushNotificationsSettings

    override fun getViewerData() {
        // used to trigger live data
        _viewerData.postValue(userManager.viewerData)
    }

    @SuppressLint("CheckResult")
    override fun retrieveViewerData() {
        _viewerDataResponse.postValue(Resource.Loading())

        userDataSource.getViewerData().subscribeWith(object : Observer<Response<ViewerQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<ViewerQuery.Data>) {
                if (t.hasErrors()) {
                    _viewerDataResponse.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    userManager.setViewerData(Converter.convertUser(t.data()?.Viewer()))
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
                    _updateAniListSettingsResponse.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    val savedUser = userManager.viewerData
                    savedUser?.options = Converter.convertUserOptions(t.data()?.UpdateUser()?.options())
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
                    _updateListSettingsResponse.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    val savedUser = userManager.viewerData
                    savedUser?.mediaListOptions = Converter.convertMediaListOptions(t.data()?.UpdateUser()?.mediaListOptions())
                    userManager.setViewerData(savedUser)

                    _updateListSettingsResponse.postValue(Resource.Success(true))
                    _viewerData.postValue(userManager.viewerData)
                }
            }

            override fun onError(e: Throwable) {
                _updateListSettingsResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    override fun setAppSettings(appColorTheme: AppColorTheme, homeShowWatching: Boolean, homeShowReading: Boolean) {
        appSettingsManager.apply {
            setAppColorTheme(appColorTheme)
            setHomeShowWatching(homeShowWatching)
            setHomeShowReading(homeShowReading)
        }
    }

    override fun setPushNotificationsSettings(
        pushNotifAiring: Boolean,
        pushNotifActivity: Boolean,
        pushNotifForum: Boolean,
        pushNotifFollows: Boolean
    ) {
        appSettingsManager.setPushNotificationsSettings(PushNotificationsSettings(pushNotifAiring, pushNotifActivity, pushNotifForum, pushNotifFollows))
    }
}