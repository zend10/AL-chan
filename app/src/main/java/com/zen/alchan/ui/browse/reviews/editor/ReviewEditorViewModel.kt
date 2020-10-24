package com.zen.alchan.ui.browse.reviews.editor

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.User
import type.MediaType

class ReviewEditorViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var reviewId: Int? = null
    var mediaId: Int? = null

    var reviewString = ""
    var summaryString = ""
    var score = 0
    var isPrivate = false

    val reviewDetailData by lazy {
        mediaRepository.reviewDetailData
    }

    val saveReviewResponse by lazy {
        mediaRepository.saveReviewResponse
    }

    val deleteReviewResponse by lazy {
        mediaRepository.deleteReviewResponse
    }

    fun getReviewDetail() {
        if (reviewId != null && reviewId != 0) {
            mediaRepository.getReviewDetail(reviewId!!)
        }
    }

    fun saveReview() {
        if (mediaId != null) {
            mediaRepository.saveReview(if (reviewId == 0) null else reviewId, mediaId!!, reviewString, summaryString, score, isPrivate)
        }
    }

    fun deleteReview() {
        if (reviewId != null && reviewId != 0) {
            mediaRepository.deleteReview(reviewId!!)
        }
    }
}