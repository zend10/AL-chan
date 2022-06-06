package com.zen.alchan.ui.base

interface BrowseNavigationManager {

    fun backStackCount(): Int
    fun pushBrowseScreenPage(page: Page, id: Int?)
    fun popBrowseScreenPage()

    enum class Page {
        MEDIA,
        MEDIA_CHARACTERS,
        MEDIA_STAFF,
        CHARACTER,
        CHARACTER_MEDIA,
        STAFF,
        STAFF_CHARACTER,
        STAFF_MEDIA,
        USER,
        STUDIO,
        ANIME_MEDIA_LIST,
        MANGA_MEDIA_LIST,
        FOLLOWING,
        FOLLOWERS,
        USER_STATS,
        FAVORITE_ANIME,
        FAVORITE_MANGA,
        FAVORITE_CHARACTERS,
        FAVORITE_STAFF,
        FAVORITE_STUDIOS
    }
}