package com.zen.alchan.data.repository

interface MediaRepository {
    val genreList: List<String>
    val genreListLastRetrieved: Long?
    fun getGenre()
}