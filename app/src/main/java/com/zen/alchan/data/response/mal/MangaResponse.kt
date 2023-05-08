package com.zen.alchan.data.response.mal

import com.google.gson.annotations.SerializedName

data class MangaResponse(
    @SerializedName("mal_id")
    val malId: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("serializations")
    val serializations: List<MangaSerializationResponse>? = null
)

data class MangaSerializationResponse(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("url")
    val url: String? = null
)