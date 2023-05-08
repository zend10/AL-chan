package com.zen.alchan.data.response

import com.google.gson.annotations.SerializedName

data class Manga(
    val malId: Int = 0,
    val title: String = "",
    val serializations: List<MangaSerialization> = listOf()
)

data class MangaSerialization(
    val name: String = "",
    val url: String = ""
)