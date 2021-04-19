package com.zen.alchan.ui.base

interface NavigationManager {

    fun navigate(page: Page)

    enum class Page {
        PAGE_MAIN,
        PAGE_HOME,
        PAGE_ANIME,
        PAGE_MANGA,
        PAGE_SOCIAL,
        PAGE_PROFILE,
        PAGE_BROWSE
    }
}