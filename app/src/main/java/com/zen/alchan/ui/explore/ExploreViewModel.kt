package com.zen.alchan.ui.explore

import androidx.lifecycle.ViewModel
import com.zen.alchan.helper.enums.BrowsePage

class ExploreViewModel : ViewModel() {

    var selectedExplorePage: BrowsePage? = null

    var explorePageArray = arrayOf(
        BrowsePage.ANIME.name, BrowsePage.MANGA.name, BrowsePage.CHARACTER.name, BrowsePage.STAFF.name, BrowsePage.STUDIO.name
    )

    fun search() {

    }
}