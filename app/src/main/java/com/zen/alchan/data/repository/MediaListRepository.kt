package com.zen.alchan.data.repository

import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import io.reactivex.Completable
import io.reactivex.Observable
import type.MediaListStatus
import type.MediaType

interface MediaListRepository {
    val defaultAnimeList: List<String>
    val defaultAnimeListSplitCompletedSectionByFormat: List<String>
    val defaultMangaList: List<String>
    val defaultMangaListSplitCompletedSectionByFormat: List<String>
    val refreshMediaListTrigger: Observable<Pair<com.zen.alchan.helper.enums.MediaType, MediaList?>>
    fun getMediaListCollection(userId: Int, mediaType: MediaType): Observable<MediaListCollection>
    fun getMediaWithMediaList(mediaId: Int, mediaType: MediaType): Observable<Media>
    fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null
    ): Completable
    fun updateMediaListEntry(
        mediaType: com.zen.alchan.helper.enums.MediaType,
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
    fun deleteMediaListEntry(mediaType: com.zen.alchan.helper.enums.MediaType, id: Int): Completable
    fun updateMediaListScore(mediaType: com.zen.alchan.helper.enums.MediaType, id: Int, score: Double, advancedScores: List<Double>?): Observable<MediaList>
}