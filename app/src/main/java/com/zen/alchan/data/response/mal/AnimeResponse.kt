package com.zen.alchan.data.response.mal

import com.google.gson.annotations.SerializedName

data class AnimeResponse(
    @SerializedName("data")
    val data: AnimeDataResponse? = null
)

data class AnimeDataResponse(
    @SerializedName("mal_id")
    val malId: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("theme")
    val theme: AnimeThemeResponse? = null
)

data class AnimeThemeResponse(
    @SerializedName("openings")
    val openings: List<String>? = null,
    @SerializedName("endings")
    val endings: List<String>? = null
)