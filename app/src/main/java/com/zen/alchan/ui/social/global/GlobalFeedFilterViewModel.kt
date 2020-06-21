package com.zen.alchan.ui.social.global

import androidx.lifecycle.ViewModel
import com.zen.alchan.R
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.BestFriend
import type.ActivityType

class GlobalFeedFilterViewModel(private val userRepository: UserRepository) : ViewModel() {

    var selectedActivityType: ArrayList<ActivityType>? = null
    var selectedFilterIndex: Int? = null

    val activityTypeList = arrayListOf(
        null, arrayListOf(ActivityType.TEXT), arrayListOf(ActivityType.ANIME_LIST, ActivityType.MANGA_LIST)
    )

    val activityTypeArray = arrayOf(
        R.string.all, R.string.status, R.string.list
    )

    var bestFriends = ArrayList<BestFriend>()

    val savedBestFriends: List<BestFriend>?
        get() = userRepository.bestFriends

    fun reinitBestFriends() {
        bestFriends.clear()
        bestFriends.add(BestFriend(null, "Global", null))
        bestFriends.add(BestFriend(null, "Following", null))
        savedBestFriends?.forEach {
            bestFriends.add(it)
        }
    }
}