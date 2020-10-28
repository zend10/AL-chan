package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AnimePromo(
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("image_url")
    @Expose
    val imageUrl: String,
    @SerializedName("video_url")
    @Expose
    val videoUrl: String
)