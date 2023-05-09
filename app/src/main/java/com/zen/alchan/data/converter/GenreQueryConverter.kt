package com.zen.alchan.data.converter

import com.zen.alchan.GenreQuery
import com.zen.alchan.data.response.Genre

fun GenreQuery.Data.convert(): List<Genre> {
    return GenreCollection?.mapNotNull { Genre(it ?: "") } ?: listOf()
}