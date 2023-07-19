package com.zen.alchan.data.converter

import com.zen.alchan.data.response.TrackSearch
import com.zen.alchan.data.response.spotify.TrackSearchResponse

fun TrackSearchResponse.convert(): TrackSearch {
    return TrackSearch(
        trackUrl = tracks?.items?.firstOrNull()?.externalUrls?.spotify ?: ""
    )
}