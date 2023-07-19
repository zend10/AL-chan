package com.zen.alchan.data.repository

import android.net.Uri
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.type.MediaListStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface MediaListRepository {
    val defaultAnimeList: List<String>
    val defaultAnimeListSplitCompletedSectionByFormat: List<String>
    val defaultMangaList: List<String>
    val defaultMangaListSplitCompletedSectionByFormat: List<String>
    val refreshMediaListTrigger: Observable<Pair<MediaType, MediaList?>>
    val releasingTodayTrigger: Observable<Unit>

    fun getMediaListCollection(source: Source = Source.NETWORK, userId: Int, mediaType: MediaType): Observable<MediaListCollection>
    fun updateCacheMediaList(mediaType: MediaType, mediaListCollection: MediaListCollection)
    fun getMediaWithMediaList(mediaId: Int): Observable<Media>
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
    fun updateMediaListStatus(mediaType: MediaType, mediaId: Int, status: MediaListStatus): Observable<MediaList>

    fun getListStyle(mediaType: MediaType): Observable<ListStyle>
    fun setListStyle(mediaType: MediaType, newListStyle: ListStyle)
    fun getListBackground(mediaType: MediaType): Observable<NullableItem<Uri>>
    fun setListBackground(mediaType: MediaType, newUri: Uri?): Observable<Unit>
    fun getMediaFilter(mediaType: MediaType): Observable<MediaFilter>
    fun setMediaFilter(mediaType: MediaType, newMediaFilter: MediaFilter)
    fun triggerReleasingToday()
}