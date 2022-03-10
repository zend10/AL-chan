package com.zen.alchan.data.response.anilist

import type.MediaListStatus

data class StatusDistribution(
    val status: MediaListStatus? = null,
    val amount: Int = 0
)