package com.zen.alchan.ui.social

import androidx.lifecycle.ViewModel
import com.zen.alchan.R
import com.zen.alchan.data.repository.*
import com.zen.alchan.data.response.User
import com.zen.alchan.data.response.UserAvatar
import com.zen.alchan.helper.pojo.ActivityItem
import com.zen.alchan.helper.pojo.ActivityReply
import com.zen.alchan.helper.pojo.BestFriend
import type.ActivityType
import type.LikeableType

class SocialViewModel(private val mediaRepository: MediaRepository,
                      private val userRepository: UserRepository,
                      private val socialRepository: SocialRepository,
                      private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    val textActivityText: String
        get() = socialRepository.textActivityText

    val listActivityText: String
        get() = socialRepository.listActivityText

    val messageActivityText: String
        get() = socialRepository.messageActivityText

    var isInit = false

    var selectedActivityType: ArrayList<ActivityType>? = null
    var selectedBestFriend: BestFriend? = null

    var bestFriends = ArrayList<BestFriend>()

    val activityTypeList = arrayListOf(
        null, arrayListOf(ActivityType.TEXT), arrayListOf(ActivityType.ANIME_LIST, ActivityType.MANGA_LIST)
    )

    val activityTypeArray = arrayOf(
        R.string.all, R.string.status, R.string.list
    )

    val savedBestFriends: List<BestFriend>?
        get() = userRepository.bestFriends

    val activityList = ArrayList<ActivityItem>()

    val mostTrendingAnimeBannerLiveData by lazy {
        mediaRepository.mostTrendingAnimeBannerLivaData
    }

    val bestFriendChangedNotifier by lazy {
        userRepository.bestFriendChangedNotifier
    }

    val notifyFriendsActivity by lazy {
        socialRepository.notifyFriendsActivity
    }

    val friendsActivityResponse by lazy {
        socialRepository.friendsActivityResponse
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

    val currentUserId: Int?
        get() = userRepository.currentUser?.id

    val enableSocial: Boolean
        get() = appSettingsRepository.appSettings.showSocialTabAutomatically != false

    fun initData() {
        if (!isInit) {
            isInit = true
            reinitBestFriends()
            socialRepository.getFriendsActivity(selectedActivityType, if (selectedBestFriend != null) selectedBestFriend?.id!! else null)
        }
    }

    fun retrieveFriendsActivity() {
        socialRepository.getFriendsActivity(selectedActivityType, if (selectedBestFriend != null) selectedBestFriend?.id!! else null)
    }

    fun reinitBestFriends() {
        bestFriends.clear()
        bestFriends.add(BestFriend(null, null, null))
        savedBestFriends?.forEach {
            bestFriends.add(it)
        }
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