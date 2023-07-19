package com.zen.alchan.ui.filter

import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.type.ScoreFormat

data class FilterParam(
    val mediaFilter: MediaFilter,
    val mediaType: MediaType,
    val scoreFormat: ScoreFormat,
    val isUserList: Boolean,
    val isCurrentUser: Boolean
)