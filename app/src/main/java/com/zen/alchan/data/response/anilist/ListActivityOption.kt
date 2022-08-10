package com.zen.alchan.data.response.anilist

import type.MediaListStatus

data class ListActivityOption(
    val disabled: Boolean = false,
    val type: MediaListStatus? = null
)