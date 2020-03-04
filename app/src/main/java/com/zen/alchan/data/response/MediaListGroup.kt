package com.zen.alchan.data.response

import type.MediaListStatus

class MediaListGroup(
    var entries: List<MediaList>?,
    var name: String?,
    var isCustomList: Boolean?,
    var isSplitCompletedList: Boolean?,
    var status: MediaListStatus?
)