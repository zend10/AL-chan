package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList

fun SaveMediaListEntryMutation.Data.convert(): MediaList {
    return MediaList(
        id = saveMediaListEntry?.id ?: 0,
        status = saveMediaListEntry?.status,
        score = saveMediaListEntry?.score ?: 0.0,
        progress = saveMediaListEntry?.progress ?: 0,
        progressVolumes = saveMediaListEntry?.progressVolumes,
        repeat = saveMediaListEntry?.repeat ?: 0,
        priority = saveMediaListEntry?.priority ?: 0,
        private = saveMediaListEntry?.private_ ?: false,
        notes = saveMediaListEntry?.notes ?: "",
        hiddenFromStatusLists = saveMediaListEntry?.hiddenFromStatusLists ?: false,
        customLists = saveMediaListEntry?.customLists,
        advancedScores = saveMediaListEntry?.advancedScores,
        startedAt = if (saveMediaListEntry?.startedAt != null)
            FuzzyDate(year = saveMediaListEntry.startedAt.year, month = saveMediaListEntry.startedAt.month, day = saveMediaListEntry.startedAt.day)
        else
            null,
        completedAt = if (saveMediaListEntry?.completedAt != null)
            FuzzyDate(year = saveMediaListEntry.completedAt.year, month = saveMediaListEntry.completedAt.month, day = saveMediaListEntry.completedAt.day)
        else
            null,
        updatedAt = saveMediaListEntry?.updatedAt ?: 0,
        createdAt = saveMediaListEntry?.createdAt ?: 0,
        media = Media(
            idAniList = saveMediaListEntry?.media?.id ?: 0,
            type = saveMediaListEntry?.media?.type,
            isFavourite = saveMediaListEntry?.media?.isFavourite ?: false
        )
    )
}