package com.zen.alchan.ui.base

import android.content.Context

interface NavigationManager {

    fun navigateToSplash()
    fun navigateToLanding()
    fun navigateToLogin(bearerToken: String? = null)
    fun navigateToMain()
    fun navigateToBrowse()

    fun navigateToActivities()
    fun navigateToNotifications()
    fun navigateToSettings()
    fun navigateToAppSettings()
    fun navigateToAniListSettings()
    fun navigateToListsSettings()
    fun navigateToNotificationsSettings()
    fun navigateToAccountSettings()
    fun navigateToAbout()

    fun openWebView(url: String)
    fun openWebView(url: Url)

    enum class Url {
        ANILIST_WEBSITE,
        ANILIST_LOGIN,
        ANILIST_REGISTER
    }
}