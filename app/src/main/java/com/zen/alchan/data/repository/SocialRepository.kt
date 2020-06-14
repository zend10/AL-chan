package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import type.ActivityType

interface SocialRepository {
    val friendsActivityResponse: LiveData<Resource<ActivityQuery.Data?>>

    fun getFriendsActivity(typeIn: List<ActivityType>?, userIdIn: List<Int>?)
}