package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaTitle

fun MediaWithMediaListQuery.Data.convert(): Media {
    return Media(
        idAniList = media?.id ?: 0,
        idMal = media?.idMal,
        title = MediaTitle(
            romaji = media?.title?.romaji ?: "",
            english = media?.title?.english ?: "",
            native = media?.title?.native_ ?: "",
            userPreferred = media?.title?.userPreferred ?: ""
        ),
        episodes = media?.episodes,
        chapters = media?.chapters,
        volumes = media?.volumes,
        isFavourite = media?.isFavourite ?: false,
        mediaListEntry = MediaList(
            id = media?.mediaListEntry?.id ?: 0,
            status = media?.mediaListEntry?.status,
            score = media?.mediaListEntry?.score ?: 0.0,
            progress = media?.mediaListEntry?.progress ?: 0,
            progressVolumes = media?.mediaListEntry?.progressVolumes,
            repeat = media?.mediaListEntry?.repeat ?: 0,
            priority = media?.mediaListEntry?.priority ?: 0,
            private = media?.mediaListEntry?.private_ ?: false,
            hiddenFromStatusLists = media?.mediaListEntry?.hiddenFromStatusLists ?: false,
            customLists = media?.mediaListEntry?.customLists,
            advancedScores = media?.mediaListEntry?.advancedScores,
            startedAt = if (media?.mediaListEntry?.startedAt != null)
                FuzzyDate(media.mediaListEntry.startedAt.year, media.mediaListEntry.startedAt.month, media.mediaListEntry.startedAt.day)
            else
                null
            ,
            completedAt = if (media?.mediaListEntry?.completedAt != null)
                FuzzyDate(media.mediaListEntry.completedAt.year, media.mediaListEntry.completedAt.month, media.mediaListEntry.completedAt.day)
            else
                null
            ,
            updatedAt = media?.mediaListEntry?.updatedAt ?: 0,
            createdAt = media?.mediaListEntry?.createdAt ?: 0
        )
    )
}


