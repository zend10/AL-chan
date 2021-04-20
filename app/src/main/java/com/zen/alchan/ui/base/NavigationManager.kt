package com.zen.alchan.ui.base

import android.content.Context

interface NavigationManager {

    fun navigate(page: Page, params: List<String> = listOf())

    fun openWebView(url: String)

    fun openWebView(url: Url)

    enum class Page {
        SPLASH,
        LANDING,
        LOGIN,
        MAIN,
        HOME,
        ANIME,
        MANGA,
        SOCIAL,
        PROFILE,
        BROWSE
    }

    enum class Url {
        ANILIST_WEBSITE,
        ANILIST_LOGIN,
        ANILIST_REGISTER
    }
}