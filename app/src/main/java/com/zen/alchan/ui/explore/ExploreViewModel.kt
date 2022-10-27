package com.zen.alchan.ui.explore

import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.ui.base.BaseViewModel

class ExploreViewModel() : BaseViewModel<SearchCategory>() {

    private var currentSearchCategory = SearchCategory.ANIME

    override fun loadData(param: SearchCategory) {
        this.currentSearchCategory = param

        
    }
}