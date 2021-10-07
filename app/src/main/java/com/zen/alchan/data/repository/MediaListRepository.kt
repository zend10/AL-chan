package com.zen.alchan.data.repository

import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import io.reactivex.Observable
import type.MediaType

interface MediaListRepository {
    val defaultAnimeList: List<String>
    val defaultAnimeListSplitCompletedSectionByFormat: List<String>
    val defaultMangaList: List<String>
    val defaultMangaListSplitCompletedSectionByFormat: List<String>
    fun getMediaListCollection(userId: Int, mediaType: MediaType): Observable<MediaListCollection>
    fun getMediaWithMediaList(mediaId: Int, mediaType: MediaType): Observable<Media>
}