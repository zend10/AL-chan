package com.zen.alchan.data.localstorage

interface MediaManager {
    val genreList: List<String?>
    val genreListLastRetrieved: Long?
    fun setGenreList(genres: List<String?>)
}