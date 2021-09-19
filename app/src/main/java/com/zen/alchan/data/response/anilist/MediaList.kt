package com.zen.alchan.data.response.anilist

import androidx.annotation.DrawableRes
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.roundToOneDecimal
import type.MediaListStatus

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
    val media: Media = Media()
) {
    fun getScore(): String {
        return score.roundToOneDecimal()
    }

    @DrawableRes
    fun getScoreSmiley(): Int {
        return when (score) {
            1.0 -> R.drawable.ic_sad
            2.0 -> R.drawable.ic_neutral
            3.0 -> R.drawable.ic_happy
            else -> R.drawable.ic_puzzled
        }
    }
}