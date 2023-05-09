package com.zen.alchan.helper.enums

import android.content.Context
import com.zen.alchan.R
import com.zen.alchan.type.MediaSort

enum class Sort {
    FOLLOW_LIST_SETTINGS,
    TITLE,
    SCORE,
    PROGRESS,
    LAST_UPDATED,
    LAST_ADDED,
    START_DATE,
    COMPLETED_DATE,
    RELEASE_DATE,
    AVERAGE_SCORE,
    POPULARITY,
    FAVORITES,
    TRENDING,
    PRIORITY,
    NEXT_AIRING
}

fun Sort.getString(context: Context): String {
    return context.getString(getStringResource())
}

fun Sort.getStringResource(): Int {
    return when (this) {
        Sort.FOLLOW_LIST_SETTINGS -> R.string.follow_list_settings
        Sort.TITLE -> R.string.title
        Sort.SCORE -> R.string.score
        Sort.PROGRESS -> R.string.progress
        Sort.LAST_UPDATED -> R.string.last_updated
        Sort.LAST_ADDED -> R.string.last_added
        Sort.START_DATE -> R.string.start_date
        Sort.COMPLETED_DATE -> R.string.completed_date
        Sort.RELEASE_DATE -> R.string.release_date
        Sort.AVERAGE_SCORE -> R.string.average_score
        Sort.POPULARITY -> R.string.popularity
        Sort.FAVORITES -> R.string.favorites
        Sort.TRENDING -> R.string.trending
        Sort.PRIORITY -> R.string.priority
        Sort.NEXT_AIRING -> R.string.next_airing
    }
}

fun Sort.getAniListMediaSort(orderByDescending: Boolean): MediaSort? {
    return if (orderByDescending) {
        when (this) {
            Sort.FOLLOW_LIST_SETTINGS -> null
            Sort.TITLE -> null
            Sort.SCORE -> MediaSort.SCORE_DESC
            Sort.PROGRESS -> null
            Sort.LAST_UPDATED -> null
            Sort.LAST_ADDED -> MediaSort.ID_DESC
            Sort.START_DATE -> null
            Sort.COMPLETED_DATE -> null
            Sort.RELEASE_DATE -> MediaSort.START_DATE_DESC
            Sort.AVERAGE_SCORE -> MediaSort.SCORE_DESC
            Sort.POPULARITY -> MediaSort.POPULARITY_DESC
            Sort.FAVORITES -> MediaSort.FAVOURITES_DESC
            Sort.TRENDING -> MediaSort.TRENDING_DESC
            Sort.PRIORITY -> null
            Sort.NEXT_AIRING -> null
        }
    } else {
        when (this) {
            Sort.FOLLOW_LIST_SETTINGS -> null
            Sort.TITLE -> null
            Sort.SCORE -> MediaSort.SCORE
            Sort.PROGRESS -> null
            Sort.LAST_UPDATED -> null
            Sort.LAST_ADDED -> MediaSort.ID
            Sort.START_DATE -> null
            Sort.COMPLETED_DATE -> null
            Sort.RELEASE_DATE -> MediaSort.START_DATE
            Sort.AVERAGE_SCORE -> MediaSort.SCORE
            Sort.POPULARITY -> MediaSort.POPULARITY
            Sort.FAVORITES -> MediaSort.FAVOURITES
            Sort.TRENDING -> MediaSort.TRENDING
            Sort.PRIORITY -> null
            Sort.NEXT_AIRING -> null
        }
    }
}