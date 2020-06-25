package com.zen.alchan.ui.profile.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.repository.OtherUserRepository
import com.zen.alchan.data.repository.UserRepository

class UserReviewsViewModel(private val userRepository: UserRepository,
                           private val otherUserRepository: OtherUserRepository
) : ViewModel() {

    var otherUserId: Int? = null

    var page = 1
    var hasNextPage = true
    var isInit = false
    var userReviews = ArrayList<UserReviewsQuery.Review>()

    val viewerReviewsResponse by lazy {
        userRepository.viewerReviewsResponse
    }

    val triggerRefreshReviews by lazy {
        userRepository.triggerRefreshReviews
    }

    val userReviewsResponse by lazy {
        otherUserRepository.userReviewsResponse
    }

    val otherUserTriggerRefreshReviews by lazy {
        otherUserRepository.triggerRefreshReviews
    }

    fun getReviews() {
        if (hasNextPage) {
            if (otherUserId != null) {
                otherUserRepository.getReviews(otherUserId!!, page)
            } else {
                userRepository.getReviews(page)
            }
        }
    }

    fun refresh() {
        page = 1
        hasNextPage = true
        userReviews.clear()
        getReviews()
    }

    fun getReviewsObserver(): LiveData<Resource<UserReviewsQuery.Data>> {
        return if (otherUserId != null) {
            userReviewsResponse
        } else {
            viewerReviewsResponse
        }
    }

    fun getTriggerRefreshObserver(): LiveData<Boolean> {
        return if (otherUserId != null) {
            otherUserTriggerRefreshReviews
        } else {
            triggerRefreshReviews
        }
    }
}