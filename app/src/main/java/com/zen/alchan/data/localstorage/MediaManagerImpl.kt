package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.MediaTagCollection
import com.zen.alchan.helper.utils.Utility

class MediaManagerImpl(private val localStorage: LocalStorage) : MediaManager {

    override val genreList: List<String?>
        get() = localStorage.genreList ?: ArrayList()

    override val genreListLastRetrieved: Long?
        get() = localStorage.genreListLastRetrieved

    override val tagList: List<MediaTagCollection>
        get() = localStorage.tagList ?: ArrayList()

    override val tagListLastRetrieved: Long?
        get() = localStorage.tagListLastRetrieved

    override fun setGenreList(genres: List<String?>) {
        localStorage.genreList = genres
        localStorage.genreListLastRetrieved = Utility.getCurrentTimestamp()
    }

    override fun setTagList(tags: List<MediaTagCollection>) {
        localStorage.tagList = tags
        localStorage.tagListLastRetrieved = Utility.getCurrentTimestamp()
    }
}