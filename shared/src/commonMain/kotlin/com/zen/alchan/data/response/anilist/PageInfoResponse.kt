package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class PageInfoResponse(
    val currentPage: Int? = null,
    val hasNextPage: Boolean? = null,
    val lastPage: Int? = null,
    val perPage: Int? = null,
    val total: Int? = null
)
