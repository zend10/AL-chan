package com.zen.alchan.data.converter

import com.zen.alchan.MediaWithMediaListQuery
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaTitle

fun MediaWithMediaListQuery.Data.convert(): Media {
    return Media(
        idAniList = Media?.id ?: 0,
        idMal = Media?.idMal,
        title = MediaTitle(
            romaji = Media?.title?.romaji ?: "",
            english = Media?.title?.english ?: "",
            native = Media?.title?.native ?: "",
            userPreferred = Media?.title?.userPreferred ?: ""
        ),
        type = Media?.type,
        episodes = Media?.episodes,
        chapters = Media?.chapters,
        volumes = Media?.volumes,
        isFavourite = Media?.isFavourite ?: false,
        mediaListEntry = MediaList(
            id = Media?.mediaListEntry?.id,
            status = Media?.mediaListEntry?.status,
            score = Media?.mediaListEntry?.score ?: 0.0,
            progress = Media?.mediaListEntry?.progress ?: 0,
            progressVolumes = Media?.mediaListEntry?.progressVolumes,
            repeat = Media?.mediaListEntry?.repeat ?: 0,
            priority = Media?.mediaListEntry?.priority ?: 0,
            private = Media?.mediaListEntry?.private ?: false,
            notes = Media?.mediaListEntry?.notes ?: "",
            hiddenFromStatusLists = Media?.mediaListEntry?.hiddenFromStatusLists ?: false,
            customLists = Media?.mediaListEntry?.customLists,
            advancedScores = Media?.mediaListEntry?.advancedScores,
            startedAt = if (Media?.mediaListEntry?.startedAt != null)
                FuzzyDate(Media.mediaListEntry.startedAt.year, Media.mediaListEntry.startedAt.month, Media.mediaListEntry.startedAt.day)
            else
                null
            ,
            completedAt = if (Media?.mediaListEntry?.completedAt != null)
                FuzzyDate(Media.mediaListEntry.completedAt.year, Media.mediaListEntry.completedAt.month, Media.mediaListEntry.completedAt.day)
            else
                null
            ,
            updatedAt = Media?.mediaListEntry?.updatedAt ?: 0,
            createdAt = Media?.mediaListEntry?.createdAt ?: 0
        )
    )
}


