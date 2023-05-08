package com.zen.alchan.data.converter

import com.zen.alchan.data.response.Manga
import com.zen.alchan.data.response.MangaSerialization
import com.zen.alchan.data.response.mal.MangaResponse

fun MangaResponse.convert(): Manga {
    return Manga(
        malId = malId ?: 0,
        title = title ?: "",
        serializations = serializations?.map {
            MangaSerialization(
                name = it.name ?: "",
                url = it.url ?: ""
            )
        } ?: listOf()
    )
}