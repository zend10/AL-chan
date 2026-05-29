package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    val data: T? = null,
    val pageInfo: PageInfoResponse? = null
)
