package com.zen.alchan.data.converter

import com.zen.alchan.SaveMediaListEntryMutation
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList

fun SaveMediaListEntryMutation.Data.convert(): MediaList {
    return MediaList(
        id = SaveMediaListEntry?.id,
        status = SaveMediaListEntry?.status,
        score = SaveMediaListEntry?.score ?: 0.0,
        progress = SaveMediaListEntry?.progress ?: 0,
        progressVolumes = SaveMediaListEntry?.progressVolumes,
        repeat = SaveMediaListEntry?.repeat ?: 0,
        priority = SaveMediaListEntry?.priority ?: 0,
        private = SaveMediaListEntry?.private ?: false,
        notes = SaveMediaListEntry?.notes ?: "",
        hiddenFromStatusLists = SaveMediaListEntry?.hiddenFromStatusLists ?: false,
        customLists = SaveMediaListEntry?.customLists,
        advancedScores = SaveMediaListEntry?.advancedScores,
        startedAt = if (SaveMediaListEntry?.startedAt != null)
            FuzzyDate(year = SaveMediaListEntry.startedAt.year, month = SaveMediaListEntry.startedAt.month, day = SaveMediaListEntry.startedAt.day)
        else
            null,
        completedAt = if (SaveMediaListEntry?.completedAt != null)
            FuzzyDate(year = SaveMediaListEntry.completedAt.year, month = SaveMediaListEntry.completedAt.month, day = SaveMediaListEntry.completedAt.day)
        else
            null,
        updatedAt = SaveMediaListEntry?.updatedAt ?: 0,
        createdAt = SaveMediaListEntry?.createdAt ?: 0,
        media = Media(
            idAniList = SaveMediaListEntry?.media?.id ?: 0,
            type = SaveMediaListEntry?.media?.type,
            isFavourite = SaveMediaListEntry?.media?.isFavourite ?: false
        )
    )
}