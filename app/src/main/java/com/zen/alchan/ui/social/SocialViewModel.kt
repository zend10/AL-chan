package com.zen.alchan.ui.social

import androidx.lifecycle.ViewModel
import com.zen.alchan.R
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.repository.OtherUserRepository
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.User
import com.zen.alchan.data.response.UserAvatar
import com.zen.alchan.helper.pojo.ActivityItem
import com.zen.alchan.helper.pojo.ActivityReply
import com.zen.alchan.helper.pojo.BestFriend
import type.ActivityType

class SocialViewModel(private val mediaRepository: MediaRepository,
                      private val userRepository: UserRepository,
                      private val socialRepository: SocialRepository
) : ViewModel() {

    val TEXT_ACTIVITY = "TextActivity"
    val LIST_ACTIVITY = "ListActivity"
    val MESSAGE_ACTIVITY = "MessageActivity"

    var isInit = false

    var selectedActivityType: ArrayList<ActivityType>? = null
    var selectedBestFriend: BestFriend? = null

    var bestFriends = ArrayList<BestFriend>()

    val activityTypeList = arrayListOf(
        null, arrayListOf(ActivityType.TEXT), arrayListOf(ActivityType.ANIME_LIST, ActivityType.MANGA_LIST)
    )

    val activityTypeArray = arrayOf(
        R.string.all, R.string.text_status, R.string.list_status
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

    val friendsActivityResponse by lazy {
        socialRepository.friendsActivityResponse
    }

    val currentUserId: Int?
        get() = userRepository.viewerData.value?.id

    fun initData() {
        if (!isInit) {
            isInit = true
            reinitBestFriends()
            socialRepository.getFriendsActivity(selectedActivityType, if (selectedBestFriend != null) listOf(selectedBestFriend?.id!!) else null)
        }
    }

    fun retrieveFriendsActivity() {
        socialRepository.getFriendsActivity(selectedActivityType, if (selectedBestFriend != null) listOf(selectedBestFriend?.id!!) else null)
    }

    fun reinitBestFriends() {
        bestFriends.clear()
        bestFriends.add(BestFriend(null, null, null))
        savedBestFriends?.forEach {
            bestFriends.add(it)
        }
    }
}