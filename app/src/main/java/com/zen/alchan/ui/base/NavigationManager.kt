package com.zen.alchan.ui.base

interface NavigationManager {

    fun navigate(page: Page)

    enum class Page {
        PAGE_MAIN,
        PAGE_BROWSE
    }
}