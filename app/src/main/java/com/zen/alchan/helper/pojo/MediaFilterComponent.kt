package com.zen.alchan.helper.pojo

import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.type.ScoreFormat

data class MediaFilterComponent(
    val mediaFilter: MediaFilter,
    val mediaType: MediaType,
    val scoreFormat: ScoreFormat,
    val isUserList: Boolean,
    val hasBigList: Boolean,
    val isViewer: Boolean
)