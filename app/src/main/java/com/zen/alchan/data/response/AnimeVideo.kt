package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AnimeVideo(
    @SerializedName("promo")
    @Expose
    val promo: List<AnimePromo>
)