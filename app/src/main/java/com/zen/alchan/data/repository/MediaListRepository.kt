package com.zen.alchan.data.repository

import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import io.reactivex.Observable
import type.MediaType

interface MediaListRepository {
    fun getMediaListCollection(userId: Int, mediaType: MediaType): Observable<MediaListCollection>
}