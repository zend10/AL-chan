package com.zen.alchan.ui.profile.reviews

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.UserRepository

class ReviewsViewModel(private val userRepository: UserRepository) : ViewModel() {

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

    fun getReviews() {
        if (hasNextPage) userRepository.getReviews(page)
    }

    fun refresh() {
        page = 1
        hasNextPage = true
        userReviews.clear()
        getReviews()
    }
}