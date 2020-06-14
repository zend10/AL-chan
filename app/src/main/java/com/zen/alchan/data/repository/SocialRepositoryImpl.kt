package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.SocialDataSource
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.ActivityType

class SocialRepositoryImpl(private val socialDataSource: SocialDataSource) : SocialRepository {

    private val _friendsActivityResponse = SingleLiveEvent<Resource<ActivityQuery.Data?>>()
    override val friendsActivityResponse: LiveData<Resource<ActivityQuery.Data?>>
        get() = _friendsActivityResponse

    @SuppressLint("CheckResult")
    override fun getFriendsActivity(typeIn: List<ActivityType>?, userIdIn: List<Int>?) {
        _friendsActivityResponse.postValue(Resource.Loading())

        socialDataSource.getFriendsActivity(typeIn, userIdIn).subscribeWith(object : Observer<Response<ActivityQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<ActivityQuery.Data>) {
                if (t.hasErrors()) {
                    _friendsActivityResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _friendsActivityResponse.postValue(Resource.Success(t.data))
                }
            }

            override fun onError(e: Throwable) {
                _friendsActivityResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }
}