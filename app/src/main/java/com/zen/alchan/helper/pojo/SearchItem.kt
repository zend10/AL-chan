package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.SearchCategory

data class SearchItem(
    val media: Media = Media(),
    val character: Character = Character(),
    val staff: Staff = Staff(),
    val studio: Studio = Studio(),
    val user: User = User(),
    val searchCategory: SearchCategory = SearchCategory.ANIME
)