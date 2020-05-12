package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.MediaTagCollection

interface MediaManager {
    val genreList: List<String?>
    val genreListLastRetrieved: Long?
    val tagList: List<MediaTagCollection>
    val tagListLastRetrieved: Long?
    fun setGenreList(genres: List<String?>)
    fun setTagList(tags: List<MediaTagCollection>)
}