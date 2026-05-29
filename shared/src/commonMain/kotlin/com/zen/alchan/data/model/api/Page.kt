package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val data: T,
    val pageInfo: PageInfo = PageInfo()
)