package com.zen.alchan.ui.search

import androidx.lifecycle.ViewModel
import com.zen.alchan.helper.enums.BrowsePage

class SearchViewModel : ViewModel() {

    var selectedPage = BrowsePage.ANIME

    val searchPageList = listOf(
        BrowsePage.ANIME, BrowsePage.MANGA, BrowsePage.CHARACTER, BrowsePage.STAFF, BrowsePage.STUDIO
    )
}