package com.zen.alchan.ui.favorite

import com.zen.alchan.helper.enums.Favorite

data class FavoriteParam(
    val userId: Int,
    val favorite: Favorite
)