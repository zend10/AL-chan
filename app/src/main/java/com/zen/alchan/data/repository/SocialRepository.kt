package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import type.ActivityType
import type.LikeableType

interface SocialRepository {
    val friendsActivityResponse: LiveData<Resource<ActivityQuery.Data>>
    val activityDetailResponse: LiveData<Resource<ActivityDetailQuery.Data>>
    val toggleLikeResponse: LiveData<Resource<ToggleLikeMutation.Data>>
    val toggleActivitySubscriptionResponse: LiveData<Resource<Boolean>>
    val deleteActivityResponse: LiveData<Resource<Boolean>>

    fun getFriendsActivity(typeIn: List<ActivityType>?, userId: Int?)
    fun getActivityDetail(id: Int)
    fun toggleLike(id: Int, type: LikeableType)
    fun toggleActivitySubscription(activityId: Int, subscribe: Boolean)
    fun deleteActivity(id: Int)
}