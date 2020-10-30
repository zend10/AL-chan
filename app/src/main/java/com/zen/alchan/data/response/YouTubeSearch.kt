package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class YouTubeSearch(
    @SerializedName("items")
    @Expose
    val items: List<YouTubeSearchItem>
)

class YouTubeSearchItem(
    @SerializedName("id")
    @Expose
    val id: YouTubeSearchItemId
)

class YouTubeSearchItemId(
    @SerializedName("videoId")
    @Expose
    val videoId: String
)
