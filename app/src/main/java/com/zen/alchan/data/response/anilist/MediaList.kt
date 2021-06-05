package com.zen.alchan.data.response.anilist

import type.MediaListStatus
import type.MediaType

data class MediaList(
    val id: Int = 0,
    val status: MediaListStatus? = null,
    val score: Double = 0.0,
    val progress: Int = 0,
    val progressVolumes: Int? = null,
    val repeat: Int = 0,
    val priority: Int = 0,
    val private: Boolean = false,
    val notes: String = "",
    val hiddenFromStatusLists: Boolean = false,
    val customLists: Any? = null,
    val advancedScores: Any? = null,
    val startedAt: FuzzyDate? = null,
    val completedAt: FuzzyDate? = null,
    val updatedAt: Int = 0,
    val createdAt: Int = 0,
    val media: Media = Media.EMPTY_MEDIA
) {
    fun generateProgressAndMaxProgressText(showVolumeProgress: Boolean = false): String {
        return when (media.type) {
            MediaType.ANIME -> {
                "$progress / ${media.episodes ?: "?"}"
            }
            MediaType.MANGA -> {
                if (showVolumeProgress)
                    "$progressVolumes / ${media.volumes ?: "?"}"
                else
                    "$progress / ${media.chapters ?: "?"}"
            }
            else -> {
                "$progress / ?"
            }
        }
    }
}