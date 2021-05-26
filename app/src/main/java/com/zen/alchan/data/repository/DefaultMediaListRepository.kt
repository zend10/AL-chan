package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import io.reactivex.Observable
import type.MediaType

class DefaultMediaListRepository(private val mediaListDataSource: MediaListDataSource) : MediaListRepository {

    override fun getMediaListCollection(
        userId: Int,
        mediaType: MediaType
    ): Observable<MediaListCollection> {
        return mediaListDataSource.getMediaListCollectionQuery(userId, mediaType).map {
            it.data?.convert()
        }
    }
}