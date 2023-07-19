package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.data.response.anilist.Studio
import com.zen.alchan.helper.enums.Favorite

data class FavoriteItem(
    val anime: Media? = null,
    val manga: Media? = null,
    val character: Character? = null,
    val staff: Staff? = null,
    val studio: Studio? = null,
    val favorite: Favorite
)