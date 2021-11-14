package com.zen.alchan.ui.base

interface BrowseNavigationManager {

    fun pushBrowseScreenPage(page: Page, id: Int?)
    fun popBrowseScreenPage()

    enum class Page {
        MEDIA,
        CHARACTER,
        USER
    }
}