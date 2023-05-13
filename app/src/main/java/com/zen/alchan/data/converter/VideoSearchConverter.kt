package com.zen.alchan.data.converter

import com.zen.alchan.data.response.VideoSearch
import com.zen.alchan.data.response.youtube.VideoSearchResponse

fun VideoSearchResponse.convert(): VideoSearch {
    return VideoSearch(
        videoId = items?.firstOrNull()?.id?.videoId ?: ""
    )
}