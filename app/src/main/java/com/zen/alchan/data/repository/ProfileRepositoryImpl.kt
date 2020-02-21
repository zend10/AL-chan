package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.AuthDataSource
import com.zen.alchan.data.localstorage.LocalStorage
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.utils.Utility
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class ProfileRepositoryImpl(private val authDataSource: AuthDataSource, private val localStorage: LocalStorage) : ProfileRepository {

    private val _viewerDataResponse = SingleLiveEvent<Resource<Boolean>>()
    override val viewerDataResponse: LiveData<Resource<Boolean>>
        get() = _viewerDataResponse

    private val _viewerData = MutableLiveData<User?>()
    override val viewerData: LiveData<User?>
        get() = _viewerData

    override fun getViewerData() {
        // used to trigger live data
        _viewerData.postValue(localStorage.viewerData)
    }

    @SuppressLint("CheckResult")
    override fun retrieveViewerData() {
        _viewerDataResponse.postValue(Resource.Loading())

        authDataSource.getViewerData().subscribeWith(object : Observer<Response<ViewerQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<ViewerQuery.Data>) {
                if (t.hasErrors()) {
                    _viewerDataResponse.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    localStorage.viewerData = Converter.convertUser(t.data()?.Viewer())
                    localStorage.viewerDataLastRetrieved = Utility.getCurrentTimestamp()
                    _viewerDataResponse.postValue(Resource.Success(true))
                    _viewerData.postValue(localStorage.viewerData)
                }
            }

            override fun onError(e: Throwable) {
                _viewerDataResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }
}