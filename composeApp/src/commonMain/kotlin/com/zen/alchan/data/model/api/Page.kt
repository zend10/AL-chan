package com.zen.alchan.data.model.api

import com.zen.alchan.data.model.api.PageInfo
import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val data: T,
    val pageInfo: PageInfo = PageInfo()
)