package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.localstorage.AppSettingsManager
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver

class AuthRepositoryImpl(private val userDataSource: UserDataSource,
                         private val appSettingsManager: AppSettingsManager,
                         private val userManager: UserManager
) : AuthRepository {

    init {
        appSettingsManager.appColorThemeLiveData.observeForever { _appColorThemeLiveData.postValue(AndroidUtility.getAppColorThemeRes(it)) }
    }

    override val appColorTheme: Int
        get() = AndroidUtility.getAppColorThemeRes(appSettingsManager.appColorTheme)

    private val _appColorThemeLiveData = SingleLiveEvent<Int>()
    override val appColorThemeLiveData: LiveData<Int>
        get() = _appColorThemeLiveData

    private val _viewerDataResponse = SingleLiveEvent<Resource<Boolean>>()
    override val viewerDataResponse: LiveData<Resource<Boolean>>
        get() = _viewerDataResponse

    override val isLoggedIn: Boolean
        get() = userManager.bearerToken != null

    override fun setBearerToken(accessToken: String) {
        userManager.setBearerToken(accessToken)
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
                }
            }

            override fun onError(e: Throwable) {
                _viewerDataResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }
}