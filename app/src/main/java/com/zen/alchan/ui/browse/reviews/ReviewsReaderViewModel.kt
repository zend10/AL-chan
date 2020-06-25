package com.zen.alchan.ui.browse.reviews

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import type.ReviewRating

class ReviewsReaderViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var reviewId: Int? = null
    var reviewDetail: ReviewDetailQuery.Review? = null

    var currentUserRating: ReviewRating? = null
    var currentRating: Int? = null
    var currentRatingAmount: Int? = null

    val reviewDetailData by lazy {
        mediaRepository.reviewDetailData
    }

    val rateReviewResponse by lazy {
        mediaRepository.rateReviewResponse
    }

    fun getReviewDetail() {
        if (reviewId != null) mediaRepository.getReviewDetail(reviewId!!)
    }

    fun rateReview(rating: ReviewRating) {
        if (reviewId != null) mediaRepository.rateReview(reviewId!!, rating)
    }
}