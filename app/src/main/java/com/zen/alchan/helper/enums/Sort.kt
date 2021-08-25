package com.zen.alchan.helper.enums

import com.zen.alchan.R

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
    PRIORITY,
    NEXT_AIRING
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
        Sort.PRIORITY -> R.string.priority
        Sort.NEXT_AIRING -> R.string.next_airing
    }
}