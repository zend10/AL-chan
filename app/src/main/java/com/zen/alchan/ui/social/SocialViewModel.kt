package com.zen.alchan.ui.social

import androidx.lifecycle.ViewModel
import com.zen.alchan.R
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.repository.OtherUserRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.BestFriend
import type.ActivityType

class SocialViewModel(private val mediaRepository: MediaRepository,
                      private val userRepository: UserRepository) : ViewModel() {

    var isInit = false

    var selectedActivityType: ActivityType? = null
    var bestFriends = ArrayList<BestFriend>()

    val savedBestFriends: List<BestFriend>?
        get() = userRepository.bestFriends

    val mostTrendingAnimeBannerLiveData by lazy {
        mediaRepository.mostTrendingAnimeBannerLivaData
    }

    val bestFriendChangedNotfier by lazy {
        userRepository.bestFriendChangedNotifier
    }

    fun initData() {
        if (!isInit) {
            isInit = true
            reinitBestFriends()
            // retrieve data
        }
    }

    fun reinitBestFriends() {
        bestFriends.clear()
        bestFriends.add(BestFriend(null, null, null))
        savedBestFriends?.forEach {
            bestFriends.add(it)
        }
    }
}