package com.zen.alchan.data.repository

import android.net.Uri
import com.zen.alchan.data.converter.convert
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.getAniListMediaType
import com.zen.alchan.helper.pojo.NullableItem
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

    private val userIdToAnimeListCollectionMap = HashMap<Int, MediaListCollection>()
    private val userIdToMangaListCollectionMap = HashMap<Int, MediaListCollection>()

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
        val isViewer = userManager.viewerData?.data?.id == userId
        return if (source == Source.CACHE) {
            val mediaListCollection = if (isViewer) {
                when (mediaType) {
                    MediaType.ANIME -> userManager.animeList?.data
                    MediaType.MANGA -> userManager.mangaList?.data
                } ?: return Observable.error(NotInStorageException())
            } else {
                when (mediaType) {
                    MediaType.ANIME -> userIdToAnimeListCollectionMap[userId]
                    MediaType.MANGA -> userIdToMangaListCollectionMap[userId]
                } ?: return Observable.error(NotInStorageException())
            }

            Observable.just(mediaListCollection)
        } else {
            // for other person's list, simply get from memory cache if exist
            if (!isViewer) {
                if (mediaType == MediaType.ANIME && userIdToAnimeListCollectionMap.containsKey(userId)) {
                    return Observable.just(userIdToAnimeListCollectionMap[userId])
                }

                if (mediaType == MediaType.MANGA && userIdToMangaListCollectionMap.containsKey(userId)) {
                    return Observable.just(userIdToMangaListCollectionMap[userId])
                }
            }

            mediaListDataSource.getMediaListCollectionQuery(userId, mediaType.getAniListMediaType()).map {
                val newMediaListCollection = it.data?.convert()

                if (newMediaListCollection != null && isViewer) {
                    when (mediaType) {
                        MediaType.ANIME -> userManager.animeList = SaveItem(newMediaListCollection)
                        MediaType.MANGA -> userManager.mangaList = SaveItem(newMediaListCollection)
                    }
                }

                if (newMediaListCollection != null && !isViewer) {
                    when (mediaType) {
                        MediaType.ANIME -> userIdToAnimeListCollectionMap[userId] = newMediaListCollection
                        MediaType.MANGA -> userIdToMangaListCollectionMap[userId] = newMediaListCollection
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

    override fun getMediaWithMediaList(mediaId: Int): Observable<Media> {
        return mediaListDataSource.getMediaWithMediaListQuery(mediaId).map {
            it.data?.convert()
        }
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

    override fun getListStyle(mediaType: MediaType): Observable<ListStyle> {
        return Observable.just(
            when (mediaType) {
                MediaType.ANIME -> userManager.animeListStyle
                MediaType.MANGA -> userManager.mangaListStyle
            }
        )
    }

    override fun setListStyle(mediaType: MediaType, newListStyle: ListStyle) {
        when (mediaType) {
            MediaType.ANIME -> userManager.animeListStyle = newListStyle
            MediaType.MANGA -> userManager.mangaListStyle = newListStyle
        }
    }

    override fun getListBackground(mediaType: MediaType): Observable<NullableItem<Uri>> {
        return when (mediaType) {
            MediaType.ANIME -> {
                if (userManager.animeListStyle.useBackgroundImage) {
                    userManager.animeListBackground
                } else {
                    Observable.just(NullableItem(null))
                }
            }
            MediaType.MANGA -> {
                if (userManager.mangaListStyle.useBackgroundImage) {
                    userManager.mangaListBackground
                } else {
                    Observable.just(NullableItem(null))
                }
            }
        }
    }

    override fun setListBackground(mediaType: MediaType, newUri: Uri?): Observable<Unit> {
        return when (mediaType) {
            MediaType.ANIME -> userManager.saveAnimeListBackground(newUri)
            MediaType.MANGA -> userManager.saveMangaListBackground(newUri)
        }
    }

    override fun getMediaFilter(mediaType: MediaType): Observable<MediaFilter> {
        return Observable.just(
            when (mediaType) {
                MediaType.ANIME -> userManager.animeFilter
                MediaType.MANGA -> userManager.mangaFilter
            }
        )
    }

    override fun setMediaFilter(mediaType: MediaType, newMediaFilter: MediaFilter) {
        when (mediaType) {
            MediaType.ANIME -> userManager.animeFilter = newMediaFilter
            MediaType.MANGA -> userManager.mangaFilter = newMediaFilter
        }
    }
}