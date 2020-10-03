package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MangaDetails(
    @SerializedName("mal_id")
    @Expose
    val madId: Int,
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("serializations")
    @Expose
    val serializations: List<MangaSerialization>
)