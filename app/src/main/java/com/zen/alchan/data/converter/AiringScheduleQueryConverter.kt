package com.zen.alchan.data.converter

import com.zen.alchan.AiringScheduleQuery
import com.zen.alchan.data.response.anilist.*

fun AiringScheduleQuery.Data.convert(): Page<AiringSchedule> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.airingSchedules?.filterNotNull()?.map {
            AiringSchedule(
                id = it.id,
                airingAt = it.airingAt,
                timeUntilAiring = it.timeUntilAiring,
                episode = it.episode,
                media = it.media?.let {
                    Media(
                        idAniList = it.id,
                        title = MediaTitle(
                            romaji = it.title?.romaji ?: "",
                            english = it.title?.english ?: "",
                            native = it.title?.native ?: "",
                            userPreferred = it.title?.userPreferred ?: ""
                        ),
                        season = it.season,
                        seasonYear = it.seasonYear,
                        bannerImage = it.bannerImage ?: "",
                        isAdult = it.isAdult ?: false,
                        externalLinks = it.externalLinks?.filterNotNull()?.map {
                            MediaExternalLink(
                                id = it.id,
                                url = it.url ?: "",
                                site = it.site,
                                siteId = it.siteId ?: 0,
                                type = it.type,
                                language = it.language ?: "",
                                color = it.color ?: "",
                                icon = it.icon ?: ""
                            )
                        } ?: listOf(),
                        mediaListEntry = if (it.mediaListEntry != null) {
                            MediaList(status = it.mediaListEntry.status)
                        } else {
                            null
                        }
                    )
                } ?: Media()
            )
        } ?: listOf()
    )
}