package com.zen.alchan.ui.browse.media.reviews

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import type.ReviewSort

class MediaReviewsViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var mediaId: Int? = null
    var page = 1
    var hasNextPage = true
    var selectedSort = ReviewSort.CREATED_AT_DESC

    var isInit = false
    var mediaReviews = ArrayList<MediaReviewsQuery.Node?>()

    val sortReviewArray = arrayOf(
        "NEWEST",
        "OLDEST",
        "LAST UPDATED",
        "MOST UPVOTE",
        "LEAST UPVOTE",
        "HIGHEST SCORE",
        "LOWEST SCORE"
    )

    val sortReviewList = arrayListOf(
        ReviewSort.CREATED_AT_DESC,
        ReviewSort.CREATED_AT,
        ReviewSort.UPDATED_AT_DESC,
        ReviewSort.RATING_DESC,
        ReviewSort.RATING,
        ReviewSort.SCORE_DESC,
        ReviewSort.SCORE
    )

    val mediaReviewsData by lazy {
        mediaRepository.mediaReviewsData
    }

    val triggerMediaReview by lazy {
        mediaRepository.triggerMediaReview
    }

    fun getMediaReviews() {
        if (hasNextPage && mediaId != null) mediaRepository.getMediaReviews(mediaId!!, page, listOf(selectedSort))
    }

    fun refresh() {
        mediaReviews.clear()
        page = 1
        hasNextPage = true
        getMediaReviews()
    }
}