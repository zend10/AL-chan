package com.zen.alchan.data.response.anilist

import com.zen.alchan.type.MediaListStatus


data class ListActivityOption(
    val disabled: Boolean = false,
    val type: MediaListStatus? = null
)