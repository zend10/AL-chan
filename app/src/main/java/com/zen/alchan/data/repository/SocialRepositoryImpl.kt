package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.SocialDataSource
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.utils.AndroidUtility
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.ActivityType

class SocialRepositoryImpl(private val socialDataSource: SocialDataSource,
                           private val userManager: UserManager) : SocialRepository {

    private val _friendsActivityResponse = SingleLiveEvent<Resource<ActivityQuery.Data>>()
    override val friendsActivityResponse: LiveData<Resource<ActivityQuery.Data>>
        get() = _friendsActivityResponse

    private val _activityDetailResponse = SingleLiveEvent<Resource<ActivityDetailQuery.Data>>()
    override val activityDetailResponse: LiveData<Resource<ActivityDetailQuery.Data>>
        get() = _activityDetailResponse

    @SuppressLint("CheckResult")
    override fun getFriendsActivity(typeIn: List<ActivityType>?, userIdIn: List<Int>?) {
        _friendsActivityResponse.postValue(Resource.Loading())

        socialDataSource.getFriendsActivity(
            typeIn,
            userIdIn,
            if (userManager.viewerData?.id != null) listOf(userManager.viewerData?.id!!) else null
        ).subscribeWith(AndroidUtility.rxApolloCallback(_friendsActivityResponse))
    }

    @SuppressLint("CheckResult")
    override fun getActivityDetail(id: Int) {
        _activityDetailResponse.postValue(Resource.Loading())
        socialDataSource.getActivityDetail(id).subscribeWith(AndroidUtility.rxApolloCallback(_activityDetailResponse))
    }
}