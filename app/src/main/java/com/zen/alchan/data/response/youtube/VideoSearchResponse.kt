package com.zen.alchan.data.response.youtube

import com.google.gson.annotations.SerializedName

data class VideoSearchResponse(
    @SerializedName("items")
    val items: List<VideoSearchItemResponse>? = null
)

data class VideoSearchItemResponse(
    @SerializedName("id")
    val id: VideoSearchItemIdResponse? = null
)

data class VideoSearchItemIdResponse(
    @SerializedName("videoId")
    val videoId: String? = null
)