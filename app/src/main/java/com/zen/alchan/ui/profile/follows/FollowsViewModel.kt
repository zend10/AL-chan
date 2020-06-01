package com.zen.alchan.ui.profile.follows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.FollowPage
import com.zen.alchan.helper.pojo.FollowsItem

class FollowsViewModel(private val userRepository: UserRepository): ViewModel() {

    lateinit var followPage: FollowPage

    var page = 1
    var hasNextPage = true
    var isInit = false
    var followsList = ArrayList<FollowsItem?>()

    val userFollowingsResponse by lazy {
        userRepository.userFollowingsResponse
    }

    val userFollowersResponse by lazy {
        userRepository.userFollowersResponse
    }

    val toggleFollowingResponse by lazy {
        userRepository.toggleFollowingResponse
    }

    val toggleFollowerResponse by lazy {
        userRepository.toggleFollowerResponse
    }

    fun getItem() {
        if (!hasNextPage) {
            return
        }

        if (followPage == FollowPage.FOLLOWING) {
            userRepository.getUserFollowings(page)
        } else {
            userRepository.getUserFollowers(page)
        }
    }

    fun toggleFollow(userId: Int) {
        userRepository.toggleFollow(userId, followPage)
    }
}