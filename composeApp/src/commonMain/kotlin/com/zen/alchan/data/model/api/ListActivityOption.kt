package com.zen.alchan.data.model.api

import com.zen.alchan.data.enums.MediaListStatus
import kotlinx.serialization.Serializable

@Serializable
data class ListActivityOption(
    val disabled: Boolean = false,
    val type: MediaListStatus? = null
)
