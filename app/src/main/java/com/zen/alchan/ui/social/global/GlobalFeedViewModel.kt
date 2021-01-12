package com.zen.alchan.ui.social.global

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.R
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.ActivityItem
import com.zen.alchan.helper.pojo.BestFriend
import type.ActivityType
import type.LikeableType

class GlobalFeedViewModel(private val userRepository: UserRepository,
                          private val socialRepository: SocialRepository,
                          private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    var selectedActivityType: ArrayList<ActivityType>? = appSettingsRepository.userPreferences.globalActivityType
    var selectedFilterIndex: Int? = null

    val activityTypeList = arrayListOf(
        null, arrayListOf(ActivityType.TEXT), arrayListOf(ActivityType.ANIME_LIST, ActivityType.MANGA_LIST)
    )

    var bestFriends = ArrayList<BestFriend>()

    val savedBestFriends: List<BestFriend>?
        get() = userRepository.bestFriends

    var page = 1
    var hasNextPage = true
    var isInit = false
    var activityList = ArrayList<ActivityItem?>()

    val currentUserId: Int
        get() = userRepository.currentUser?.id!!

    val textActivityText: String
        get() = socialRepository.textActivityText

    val listActivityText: String
        get() = socialRepository.listActivityText

    val messageActivityText: String
        get() = socialRepository.messageActivityText

    val globalActivityListResponse by lazy {
        socialRepository.globalActivityListResponse
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

    val notifyGlobalActivity by lazy {
        socialRepository.notifyGlobalActivity
    }

    fun reinitBestFriends() {
        bestFriends.clear()
        bestFriends.add(BestFriend(null, "Global", null))
        bestFriends.add(BestFriend(null, "Following", null))
        savedBestFriends?.forEach {
            bestFriends.add(it)
        }
    }

    fun getActivities() {
        if (hasNextPage) {
            socialRepository.getGlobalActivityList(
                page,
                selectedActivityType,
                bestFriends[selectedFilterIndex ?: 0].id,
                if (selectedFilterIndex == 1) true else null
            )
        }
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

    fun changeActivityType(activityTypes: ArrayList<ActivityType>?) {
        selectedActivityType = activityTypes

        val savedUserPreferences = appSettingsRepository.userPreferences
        savedUserPreferences.globalActivityType = activityTypes
        appSettingsRepository.setUserPreferences(savedUserPreferences)
    }
}