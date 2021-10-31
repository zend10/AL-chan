package com.zen.alchan.data.repository

import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import io.reactivex.Completable
import io.reactivex.Observable
import type.MediaListStatus

interface MediaListRepository {
    val defaultAnimeList: List<String>
    val defaultAnimeListSplitCompletedSectionByFormat: List<String>
    val defaultMangaList: List<String>
    val defaultMangaListSplitCompletedSectionByFormat: List<String>
    val refreshMediaListTrigger: Observable<Pair<MediaType, MediaList?>>
    fun getMediaListCollection(source: Source = Source.NETWORK, userId: Int, mediaType: MediaType): Observable<MediaListCollection>
    fun updateCacheMediaList(mediaType: MediaType, mediaListCollection: MediaListCollection)
    fun getMediaWithMediaList(mediaId: Int, mediaType: MediaType): Observable<Media>
    fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null
    ): Completable
    fun updateMediaListEntry(
        mediaType: MediaType,
        id: Int?,
        mediaId: Int?,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        progressVolumes: Int?,
        repeat: Int,
        priority: Int,
        isPrivate: Boolean,
        notes: String,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?
    ): Observable<MediaList>
    fun deleteMediaListEntry(mediaType: MediaType, id: Int): Completable
    fun updateMediaListScore(mediaType: MediaType, id: Int, score: Double, advancedScores: List<Double>?): Observable<MediaList>
    fun updateMediaListProgress(
        mediaType: MediaType,
        id: Int,
        status: MediaListStatus?,
        repeat: Int?,
        progress: Int?,
        progressVolumes: Int?
    ): Observable<MediaList>
}