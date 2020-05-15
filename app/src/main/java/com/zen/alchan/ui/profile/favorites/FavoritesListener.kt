package com.zen.alchan.ui.profile.favorites

import com.zen.alchan.helper.enums.BrowsePage

interface FavoritesListener {
    fun passSelectedItem(id: Int, browsePage: BrowsePage)
}