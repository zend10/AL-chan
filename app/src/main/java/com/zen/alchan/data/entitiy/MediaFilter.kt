package com.zen.alchan.data.entitiy

import com.zen.alchan.helper.enums.Country
import com.zen.alchan.helper.enums.OtherLink
import com.zen.alchan.helper.enums.Sort
import type.MediaFormat
import type.MediaSeason
import type.MediaSource
import type.MediaStatus

data class MediaFilter(
    var persistChange: Boolean = false,
    var sort: Sort? = null,
    var sortDescending: Boolean = true,
    var mediaFormat: MediaFormat? = null,
    var mediaStatus: MediaStatus? = null,
    var mediaSource: MediaSource? = null,
    var country: Country? = null,
    var season: MediaSeason? = null,
    var minYear: Int? = null,
    var maxYear: Int? = null,
    var includedGenres: List<String> = listOf(),
    var excludedGenres: List<String> = listOf(),
    var includedTags: List<String> = listOf(),
    var excludedTags: List<String> = listOf(),
    var minTagPercentage: Int = 0,
    var otherLinks: List<OtherLink> = listOf(),
    var minEpisodes: Int? = null,
    var maxEpisodes: Int? = null,
    var minDuration: Int? = null,
    var maxDuration: Int? = null,
    var minAverageScore: Int? = null,
    var maxAverageScore: Int? = null,
    var minPopularity: Int? = null,
    var maxPopularity: Int? = null,
    var minUserScore: Int? = null,
    var maxUserScore: Int? = null,
    var minUserStartYear: Int? = null,
    var maxUserStartYear: Int? = null,
    var minUserCompletedYear: Int? = null,
    var maxUserCompletedYear: Int? = null,
    var minUserPriority: Int? = null,
    var maxUserPriority: Int? = null
) {
    companion object {
        val EMPTY_MEDIA_FILTER = MediaFilter()
    }
}