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
import type.LikeableType

class SocialRepositoryImpl(private val socialDataSource: SocialDataSource,
                           private val userManager: UserManager) : SocialRepository {

    private val _friendsActivityResponse = SingleLiveEvent<Resource<ActivityQuery.Data>>()
    override val friendsActivityResponse: LiveData<Resource<ActivityQuery.Data>>
        get() = _friendsActivityResponse

    private val _activityDetailResponse = SingleLiveEvent<Resource<ActivityDetailQuery.Data>>()
    override val activityDetailResponse: LiveData<Resource<ActivityDetailQuery.Data>>
        get() = _activityDetailResponse

    private val _toggleLikeResponse = SingleLiveEvent<Resource<ToggleLikeMutation.Data>>()
    override val toggleLikeResponse: LiveData<Resource<ToggleLikeMutation.Data>>
        get() = _toggleLikeResponse

    private val _toggleActivitySubscriptionResponse = SingleLiveEvent<Resource<Boolean>>()
    override val toggleActivitySubscriptionResponse: LiveData<Resource<Boolean>>
        get() = _toggleActivitySubscriptionResponse

    private val _deleteActivityResponse = SingleLiveEvent<Resource<Boolean>>()
    override val deleteActivityResponse: LiveData<Resource<Boolean>>
        get() = _deleteActivityResponse

    @SuppressLint("CheckResult")
    override fun getFriendsActivity(typeIn: List<ActivityType>?, userId: Int?) {
        _friendsActivityResponse.postValue(Resource.Loading())

        socialDataSource.getFriendsActivity(
            typeIn,
            userId,
            if (userManager.viewerData?.id != null) listOf(userManager.viewerData?.id!!) else null
        ).subscribeWith(AndroidUtility.rxApolloCallback(_friendsActivityResponse))
    }

    @SuppressLint("CheckResult")
    override fun getActivityDetail(id: Int) {
        _activityDetailResponse.postValue(Resource.Loading())
        socialDataSource.getActivityDetail(id).subscribeWith(AndroidUtility.rxApolloCallback(_activityDetailResponse))
    }

    @SuppressLint("CheckResult")
    override fun toggleLike(id: Int, type: LikeableType) {
        _toggleLikeResponse.postValue(Resource.Loading())
        socialDataSource.toggleLike(id, type).subscribeWith(AndroidUtility.rxApolloCallback(_toggleLikeResponse))
    }

    @SuppressLint("CheckResult")
    override fun toggleActivitySubscription(activityId: Int, subscribe: Boolean) {
        _toggleActivitySubscriptionResponse.postValue(Resource.Loading())
        socialDataSource.toggleActivitySubscription(
            activityId, subscribe
        ).subscribeWith(AndroidUtility.rxApolloCompletable(_toggleActivitySubscriptionResponse))
    }

    @SuppressLint("CheckResult")
    override fun deleteActivity(id: Int) {
        _deleteActivityResponse.postValue(Resource.Loading())
        socialDataSource.deleteActivity(id).subscribeWith(AndroidUtility.rxApolloCompletable(_deleteActivityResponse))
    }
}