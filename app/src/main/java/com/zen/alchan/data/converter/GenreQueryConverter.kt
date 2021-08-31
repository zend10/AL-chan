package com.zen.alchan.data.converter

import com.zen.alchan.data.response.Genre

fun GenreQuery.Data.convert(): List<Genre> {
    return genreCollection?.mapNotNull { Genre(it ?: "") } ?: listOf()
}