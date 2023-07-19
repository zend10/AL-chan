package com.zen.alchan.data.response.anilist

import com.zen.alchan.type.MediaListStatus


data class MediaListGroup(
    val entries: List<MediaList> = listOf(),
    val name: String = "",
    val isCustomList: Boolean = false,
    val isSplitCompletedList: Boolean = false,
    val status: MediaListStatus? = null
)