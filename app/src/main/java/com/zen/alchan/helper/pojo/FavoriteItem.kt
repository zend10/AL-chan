package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.BrowsePage

class FavoriteItem(
    val id: Int,
    val content: String, // can be image or text
    val favouriteOrder: Int,
    val browsePage: BrowsePage
)