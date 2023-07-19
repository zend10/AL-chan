package com.zen.alchan.ui.explore

import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.helper.enums.SearchCategory

data class ExploreParam(
    val searchCategory: SearchCategory,
    val mediaFilter: MediaFilter?
)
