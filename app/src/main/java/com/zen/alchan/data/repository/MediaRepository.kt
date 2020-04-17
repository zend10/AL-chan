package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.pojo.MediaCharacters

interface MediaRepository {
    val genreList: List<String>
    val genreListLastRetrieved: Long?

    val savedMediaData: HashMap<Int, MediaQuery.Media>

    val mediaData: LiveData<Resource<MediaQuery.Data>>
    val mediaStatus: LiveData<Resource<MediaStatusQuery.Data>>
    val mediaCharactersData: LiveData<Resource<MediaCharactersQuery.Data>>
    val mediaStaffsData: LiveData<Resource<MediaStaffsQuery.Data>>

    fun getGenre()
    fun getMedia(id: Int)
    fun checkMediaStatus(mediaId: Int)
    fun getMediaCharacters(id: Int, page: Int)
    fun getMediaStaffs(id: Int, page: Int)
}