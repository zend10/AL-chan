package com.zen.alchan.data.response.anilist

import com.zen.alchan.type.MediaListStatus


data class StatusDistribution(
    val status: MediaListStatus? = null,
    val amount: Int = 0
)