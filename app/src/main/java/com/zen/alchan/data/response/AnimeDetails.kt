package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AnimeDetails(
    @SerializedName("mal_id")
    @Expose
    val malId: Int,
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("opening_themes")
    @Expose
    val openingThemes: List<String>?,
    @SerializedName("ending_themes")
    @Expose
    val endingThemes: List<String>?
)