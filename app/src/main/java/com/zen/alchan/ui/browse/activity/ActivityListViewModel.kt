package com.zen.alchan.ui.browse.activity

import androidx.lifecycle.ViewModel
import com.zen.alchan.R
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.ActivityItem
import type.ActivityType
import type.LikeableType

class ActivityListViewModel(private val socialRepository: SocialRepository,
                            private val userRepository: UserRepository) : ViewModel() {

    var userId: Int? = null

    var page = 1
    var hasNextPage = true
    var selectedActivityType: ArrayList<ActivityType>? = null

    var isInit = false
    var activityList = ArrayList<ActivityItem?>()

    val activityTypeList = arrayListOf(
        null, arrayListOf(ActivityType.TEXT), arrayListOf(ActivityType.ANIME_LIST, ActivityType.MANGA_LIST), arrayListOf(ActivityType.MESSAGE)
    )

    val activityTypeArray = arrayOf(
        R.string.all, R.string.status, R.string.list, R.string.messages
    )

    val currentUserId: Int
        get() = userRepository.viewerData.value?.id!!

    val textActivityText: String
        get() = socialRepository.textActivityText

    val listActivityText: String
        get() = socialRepository.listActivityText

    val messageActivityText: String
        get() = socialRepository.messageActivityText

    val activityListResponse by lazy {
        socialRepository.activityListResponse
    }

    val toggleLikeResponse by lazy {
        socialRepository.toggleLikeResponse
    }

    val toggleActivitySubscriptionResponse by lazy {
        socialRepository.toggleActivitySubscriptionResponse
    }

    val deleteActivityResponse by lazy {
        socialRepository.deleteActivityResponse
    }

    fun getActivities() {
        if (userId != null && hasNextPage) socialRepository.getActivityList(page, selectedActivityType, userId!!)
    }

    fun refresh() {
        page = 1
        hasNextPage = true
        activityList.clear()
        getActivities()
    }

    fun toggleLike(id: Int) {
        socialRepository.toggleLike(id, LikeableType.ACTIVITY)
    }

    fun toggleSubscription(id: Int, subscribe: Boolean) {
        socialRepository.toggleActivitySubscription(id, subscribe)
    }

    fun deleteActivity(id: Int) {
        socialRepository.deleteActivity(id)
    }
}