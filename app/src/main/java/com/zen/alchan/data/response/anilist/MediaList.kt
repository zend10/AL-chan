package com.zen.alchan.data.response.anilist

import androidx.annotation.DrawableRes
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.roundToOneDecimal
import type.MediaListStatus

data class MediaList(
    val id: Int? = null,
    var status: MediaListStatus? = null,
    var score: Double = 0.0,
    var progress: Int = 0,
    var progressVolumes: Int? = null,
    var repeat: Int = 0,
    var priority: Int = 0,
    var private: Boolean = false,
    var notes: String = "",
    var hiddenFromStatusLists: Boolean = false,
    var customLists: Any? = null,
    var advancedScores: Any? = null,
    var startedAt: FuzzyDate? = null,
    var completedAt: FuzzyDate? = null,
    var updatedAt: Int = 0,
    var createdAt: Int = 0,
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