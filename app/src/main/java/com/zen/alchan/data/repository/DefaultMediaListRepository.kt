package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import io.reactivex.Observable
import type.MediaType

class DefaultMediaListRepository(private val mediaListDataSource: MediaListDataSource) : MediaListRepository {

    override val defaultAnimeList: List<String>
        get() = listOf(
            "Watching",
            "Rewatching",
            "Completed",
            "Paused",
            "Dropped",
            "Planning"
        )

    override val defaultAnimeListSplitCompletedSectionByFormat: List<String>
        get() = listOf(
            "Watching",
            "Rewatching",
            "Completed TV",
            "Completed Movie",
            "Completed OVA",
            "Completed ONA",
            "Completed TV Short",
            "Completed Special",
            "Completed Music",
            "Paused",
            "Dropped",
            "Planning"
        )

    override val defaultMangaList: List<String>
        get() = listOf(
            "Reading",
            "Rereading",
            "Completed",
            "Paused",
            "Dropped",
            "Planning"
        )

    override val defaultMangaListSplitCompletedSectionByFormat: List<String>
        get() = listOf(
            "Reading",
            "Rereading",
            "Completed Manga",
            "Completed Novel",
            "Completed One Shot",
            "Paused",
            "Dropped",
            "Planning"
        )

    override fun getMediaListCollection(
        userId: Int,
        mediaType: MediaType
    ): Observable<MediaListCollection> {
        return mediaListDataSource.getMediaListCollectionQuery(userId, mediaType).map {
            it.data?.convert()
        }
    }

    override fun getMediaWithMediaList(mediaId: Int, mediaType: MediaType): Observable<Media> {
        return mediaListDataSource.getMediaWithMediaListQuery(mediaId, mediaType).map {
            it.data?.convert()
        }
    }
}