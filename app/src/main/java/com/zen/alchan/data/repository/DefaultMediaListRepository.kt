package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import type.MediaListStatus
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

    private val _refreshMediaListTrigger = PublishSubject.create<Pair<com.zen.alchan.helper.enums.MediaType, MediaList?>>()
    override val refreshMediaListTrigger: Observable<Pair<com.zen.alchan.helper.enums.MediaType, MediaList?>>
        get() = _refreshMediaListTrigger

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

    override fun toggleFavorite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ): Completable {
        return mediaListDataSource.toggleFavorite(animeId, mangaId, characterId, staffId, studioId)
    }

    override fun updateMediaListEntry(
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
    ): Observable<MediaList> {
        return mediaListDataSource.updateMediaListEntry(
            id,
            mediaId,
            status,
            score,
            progress,
            progressVolumes,
            repeat,
            priority,
            isPrivate,
            notes,
            hiddenFromStatusLists,
            customLists,
            advancedScores,
            startedAt,
            completedAt
        ).map {
            val newMediaList = it.data?.convert()
            _refreshMediaListTrigger.onNext(mediaType to newMediaList)
            newMediaList
        }
    }

    override fun deleteMediaListEntry(mediaType: com.zen.alchan.helper.enums.MediaType, id: Int): Completable {
        return mediaListDataSource.deleteMediaListEntry(id).doFinally {
            _refreshMediaListTrigger.onNext(mediaType to null)
        }
    }

    override fun updateMediaListScore(mediaType: com.zen.alchan.helper.enums.MediaType, id: Int, score: Double, advancedScores: List<Double>?): Observable<MediaList> {
        return mediaListDataSource.updateMediaListScore(id = id, score = score, advancedScores = advancedScores).map {
            val newMediaList = it.data?.convert()
            _refreshMediaListTrigger.onNext(mediaType to newMediaList)
            newMediaList
        }
    }

    override fun updateMediaListProgress(
        mediaType: com.zen.alchan.helper.enums.MediaType,
        id: Int,
        progress: Int?,
        progressVolumes: Int?
    ): Observable<MediaList> {
        return mediaListDataSource.updateMediaListProgress(id, progress, progressVolumes).map {
            val newMediaList = it.data?.convert()
            _refreshMediaListTrigger.onNext(mediaType to newMediaList)
            newMediaList
        }
    }
}