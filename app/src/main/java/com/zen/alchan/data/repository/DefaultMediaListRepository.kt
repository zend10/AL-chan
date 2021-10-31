package com.zen.alchan.data.repository

import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.getAniListMediaType
import com.zen.alchan.helper.pojo.SaveItem
import com.zen.alchan.helper.utils.NotInStorageException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import type.MediaListStatus

class DefaultMediaListRepository(
    private val mediaListDataSource: MediaListDataSource,
    private val userManager: UserManager
) : MediaListRepository {

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

    private val _refreshMediaListTrigger = PublishSubject.create<Pair<MediaType, MediaList?>>()
    override val refreshMediaListTrigger: Observable<Pair<MediaType, MediaList?>>
        get() = _refreshMediaListTrigger

    override fun getMediaListCollection(
        source: Source,
        userId: Int,
        mediaType: MediaType
    ): Observable<MediaListCollection> {
        return if (source == Source.CACHE) {
            val mediaListCollection = when (mediaType) {
                MediaType.ANIME -> userManager.animeList?.data
                MediaType.MANGA -> userManager.mangaList?.data
            } ?: throw NotInStorageException()

            Observable.just(mediaListCollection)
        } else {
            mediaListDataSource.getMediaListCollectionQuery(userId, mediaType.getAniListMediaType()).map {
                val newMediaListCollection = it.data?.convert()

                if (newMediaListCollection != null) {
                    when (mediaType) {
                        MediaType.ANIME -> userManager.animeList = SaveItem(newMediaListCollection)
                        MediaType.MANGA -> userManager.mangaList = SaveItem(newMediaListCollection)
                    }
                }

                newMediaListCollection
            }
        }
    }

    override fun updateCacheMediaList(
        mediaType: MediaType,
        mediaListCollection: MediaListCollection
    ) {
        when (mediaType) {
            MediaType.ANIME -> userManager.animeList = SaveItem(mediaListCollection)
            MediaType.MANGA -> userManager.mangaList = SaveItem(mediaListCollection)
        }
    }

    override fun getMediaWithMediaList(mediaId: Int, mediaType: MediaType): Observable<Media> {
        return mediaListDataSource.getMediaWithMediaListQuery(mediaId, mediaType.getAniListMediaType()).map {
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

    override fun deleteMediaListEntry(mediaType: MediaType, id: Int): Completable {
        return mediaListDataSource.deleteMediaListEntry(id).doFinally {
            _refreshMediaListTrigger.onNext(mediaType to null)
        }
    }

    override fun updateMediaListScore(mediaType: MediaType, id: Int, score: Double, advancedScores: List<Double>?): Observable<MediaList> {
        return mediaListDataSource.updateMediaListScore(id = id, score = score, advancedScores = advancedScores).map {
            val newMediaList = it.data?.convert()
            _refreshMediaListTrigger.onNext(mediaType to newMediaList)
            newMediaList
        }
    }

    override fun updateMediaListProgress(
        mediaType: MediaType,
        id: Int,
        status: MediaListStatus?,
        repeat: Int?,
        progress: Int?,
        progressVolumes: Int?
    ): Observable<MediaList> {
        return mediaListDataSource.updateMediaListProgress(id, status, repeat, progress, progressVolumes).map {
            val newMediaList = it.data?.convert()
            _refreshMediaListTrigger.onNext(mediaType to newMediaList)
            newMediaList
        }
    }
}