package com.zen.alchan.data.converter

import com.zen.alchan.RateReviewMutation
import com.zen.alchan.data.response.anilist.Review

fun RateReviewMutation.Data.convert(): Review {
    return Review(
        id = RateReview?.id ?: 0,
        rating = RateReview?.rating ?: 0,
        ratingAmount = RateReview?.ratingAmount ?: 0,
        userRating = RateReview?.userRating
    )
}