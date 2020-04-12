package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource

interface MediaRepository {
    val genreList: List<String>
    val genreListLastRetrieved: Long?

    val savedMediaData: HashMap<Int, MediaQuery.Media>

    val mediaData: LiveData<Resource<MediaQuery.Data>>
    val mediaStatus: LiveData<Resource<MediaStatusQuery.Data>>

    fun getGenre()
    fun getMedia(id: Int)
    fun checkMediaStatus(mediaId: Int)
}