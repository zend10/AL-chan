package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.BrowsePage

class FavoriteItem(
    val id: Int?,
    val name: String?,
    val image: String?,
    val favouriteOrder: Int,
    val browsePage: BrowsePage
)