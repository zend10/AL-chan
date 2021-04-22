package com.zen.alchan.ui.base

import android.content.Context

interface NavigationManager {

    fun navigateToSplash()
    fun navigateToLanding()
    fun navigateToLogin(bearerToken: String? = null)
    fun navigateToMain()
    fun navigateToBrowse()

    fun openWebView(url: String)
    fun openWebView(url: Url)

    enum class Url {
        ANILIST_WEBSITE,
        ANILIST_LOGIN,
        ANILIST_REGISTER
    }
}