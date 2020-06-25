package com.zen.alchan.ui.reviews

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.helper.pojo.Review
import type.MediaType
import type.ReviewSort

class ReviewsViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var selectedMediaType: MediaType? = null
    var selectedSort: ReviewSort? = null

    val mediaTypeArray = arrayOf(
        "ALL",
        "ANIME",
        "MANGA"
    )

    val mediaTypeList = arrayListOf(
        null,
        MediaType.ANIME,
        MediaType.MANGA
    )

    val sortReviewArray = arrayOf(
        "NEWEST",
        "OLDEST",
        "LAST UPDATED",
        "MOST UPVOTE",
        "LEAST UPVOTE"
    )

    val sortReviewList = arrayListOf(
        ReviewSort.CREATED_AT_DESC,
        ReviewSort.CREATED_AT,
        ReviewSort.UPDATED_AT_DESC,
        ReviewSort.RATING_DESC,
        ReviewSort.RATING
    )

    var page = 1
    var hasNextPage = true
    var isInit = false

    var reviewsList = ArrayList<Review?>()

     val reviewsData by lazy {
         mediaRepository.reviewsData
     }

    fun getReviews() {
        if (hasNextPage) {
            mediaRepository.getReviews(page, 25, selectedMediaType, listOf(selectedSort ?: ReviewSort.CREATED_AT_DESC))
        }
    }
}