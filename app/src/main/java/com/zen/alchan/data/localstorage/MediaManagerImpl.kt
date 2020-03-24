package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.utils.Utility

class MediaManagerImpl(private val localStorage: LocalStorage) : MediaManager {

    override val genreList: List<String>
        get() = localStorage.genreList ?: ArrayList()

    override val genreListLastRetrieved: Long?
        get() = localStorage.genreListLastRetrieved

    override fun setGenreList(genres: List<String>) {
        localStorage.genreList = genres
        localStorage.genreListLastRetrieved = Utility.getCurrentTimestamp()
    }
}