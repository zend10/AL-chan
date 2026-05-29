package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class PageInfo(
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val lastPage: Int = 0,
    val perPage: Int = 0,
    val total: Int = 0
)