package com.zen.alchan.helper.extensions

import com.animedubs.AnimeDubs
import com.animedubs.models.DubStatus
import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Page
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.type.MediaType
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx3.rxObservable

private suspend fun applyStatuses(medias: List<Media>) {
    try {
        val anilistIds = medias.filter { it.type == MediaType.ANIME }.map { it.idAniList }
        if (anilistIds.isNotEmpty()) {
            val statuses = AnimeDubs.getStatusesByAnilistIds(anilistIds)
            medias.forEach { media ->
                if (media.type == MediaType.ANIME) {
                    val status = statuses[media.idAniList]?.status
                    if (status == DubStatus.YES || status == DubStatus.PARTIAL) {
                        media.isDubbed = true
                    }
                }
            }
        }
    } catch (e: Exception) {
        // Fallback gracefully on network errors
    }
}

fun List<Media>.applyDubStatus(): Observable<List<Media>> = rxObservable(Dispatchers.IO) {
    applyStatuses(this@applyDubStatus)
    send(this@applyDubStatus)
}

fun Page<Media>.applyDubStatus(): Observable<Page<Media>> = rxObservable(Dispatchers.IO) {
    val items = this@applyDubStatus.data ?: listOf()
    applyStatuses(items)
    send(this@applyDubStatus)
}

fun Page<MediaList>.applyDubStatusForMediaList(): Observable<Page<MediaList>> = rxObservable(Dispatchers.IO) {
    val items = this@applyDubStatusForMediaList.data?.mapNotNull { it.media } ?: listOf()
    applyStatuses(items)
    send(this@applyDubStatusForMediaList)
}

fun HomeData.applyDubStatus(): Observable<HomeData> = rxObservable(Dispatchers.IO) {
    applyStatuses(this@applyDubStatus.trendingAnime)
    applyStatuses(this@applyDubStatus.newAnime)
    send(this@applyDubStatus)
}

fun MediaListCollection.applyDubStatus(): Observable<MediaListCollection> = rxObservable(Dispatchers.IO) {
    val items = this@applyDubStatus.lists.flatMap { it.entries }.mapNotNull { it.media }
    applyStatuses(items)
    send(this@applyDubStatus)
}
