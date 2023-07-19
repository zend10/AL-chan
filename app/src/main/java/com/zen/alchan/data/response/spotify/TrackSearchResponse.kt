package com.zen.alchan.data.response.spotify

import com.google.gson.annotations.SerializedName

data class TrackSearchResponse(
    @SerializedName("tracks")
    val tracks: TrackSearchTrackResponse? = null
)

data class TrackSearchTrackResponse(
    @SerializedName("items")
    val items: List<TrackSearchTrackItemResponse>? = null
)

data class TrackSearchTrackItemResponse(
    @SerializedName("external_urls")
    val externalUrls: TrackSearchTrackItemExternalUrlResponse? = null
)

data class TrackSearchTrackItemExternalUrlResponse(
    @SerializedName("spotify")
    val spotify: String? = null
)
