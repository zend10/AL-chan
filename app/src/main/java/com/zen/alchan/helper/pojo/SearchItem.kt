package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.helper.enums.SearchCategory

data class SearchItem(
    val media: Media = Media(),
    val searchCategory: SearchCategory = SearchCategory.ANIME
)