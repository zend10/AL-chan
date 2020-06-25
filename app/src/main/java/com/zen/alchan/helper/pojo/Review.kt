package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.Media
import com.zen.alchan.data.response.User
import type.MediaType
import type.ReviewRating

class Review(
    val id: Int,
    val userId: Int,
    val mediaId: Int,
    val mediaType: MediaType?,
    val summary: String?,
    var rating: Int?,
    var ratingAmount: Int?,
    var userRating: ReviewRating?,
    val score: Int?,
    val siteUrl: String?,
    val createdAt: Int,
    val updatedAt: Int,
    val user: User?,
    val media: Media?
)