package com.zen.alchan.ui.base

import androidx.fragment.app.Fragment

interface BrowseNavigationManager {

    fun pushBrowseScreenPage(page: Page, id: Int?)
    fun popBrowseScreenPage()

    enum class Page {
        USER
    }
}